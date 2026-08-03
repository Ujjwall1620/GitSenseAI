package com.GitSenseAI.Bughunter.scan.service;

import com.GitSenseAI.Bughunter.BugDetection.dto.BugDetectionReport;
import com.GitSenseAI.Bughunter.BugDetection.service.BugDetectionService;
import com.GitSenseAI.Bughunter.Report.dto.BugReport;
import com.GitSenseAI.Bughunter.Report.service.ReportAggregationService;
import com.GitSenseAI.Bughunter.TEST.dto.TestExecutionResult;
import com.GitSenseAI.Bughunter.TEST.service.TestExecutionService;
import com.GitSenseAI.Bughunter.failurecorrelation.dto.FailureAnalysisResult;
import com.GitSenseAI.Bughunter.failurecorrelation.service.TestFailureCorrelatorService;
import com.GitSenseAI.Bughunter.failurecorrelation.service.TestFailureReviewService;
import com.GitSenseAI.Bughunter.fixsuggestion.dto.FixSuggestionReport;
import com.GitSenseAI.Bughunter.fixsuggestion.service.FixSuggestionService;
import com.GitSenseAI.Bughunter.scan.dto.ScanRequest;
import com.GitSenseAI.Bughunter.scan.dto.ScanResult;
import com.GitSenseAI.Bughunter.staticanalysis.dto.StaticAnalysisReport;
import com.GitSenseAI.Bughunter.staticanalysis.service.StaticAnalysisService;
import com.GitSenseAI.Retriever.EMBEDDING.dto.EmbeddingResponse;
import com.GitSenseAI.Retriever.EMBEDDING.service.EmbeddingGenerationService;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.service.KnowledgeGraphService;
import com.GitSenseAI.Retriever.PARSER.DTO.ParseRequest;
import com.GitSenseAI.Retriever.PARSER.DTO.ParseResponse;
import com.GitSenseAI.Retriever.PARSER.Service.ParserService;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.GitRepoRequest;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.RepositoryContext;
import com.GitSenseAI.Retriever.REPOSITORY.Service.RepositoryService;
import com.GitSenseAI.Retriever.VECTOR.dto.VectorStoreSaveResponse;
import com.GitSenseAI.Retriever.VECTOR.service.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Runs the full scan (tests, static analysis, AI review, fix suggestions)
 * AND embeds + stores the same Knowledge Graph for later RAG querying —
 * all from a single clone/parse/graph-build, so calling both scan and
 * query no longer requires re-cloning the same repository twice.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScanOrchestrationService {

    private final RepositoryService repositoryService;
    private final ParserService parserService;
    private final KnowledgeGraphService knowledgeGraphService;
    private final TestExecutionService testExecutionService;
    private final TestFailureCorrelatorService testFailureCorrelatorService;
    private final TestFailureReviewService testFailureReviewService;
    private final StaticAnalysisService staticAnalysisService;
    private final BugDetectionService bugDetectionService;
    private final ReportAggregationService reportAggregationService;
    private final FixSuggestionService fixSuggestionService;
    private final EmbeddingGenerationService embeddingGenerationService;
    private final VectorStoreService vectorStoreService;

    public ScanResult scan(ScanRequest scanRequest) {
        log.info("Starting scan for: {}", scanRequest.repositoryUrl());

        RepositoryContext repositoryContext = repositoryService.processRepository(
                new GitRepoRequest(scanRequest.repositoryUrl())
        );

        ParseResponse parseResponse = parserService.parseRepository(new ParseRequest(repositoryContext));
        KnowledgeGraphIndex index = knowledgeGraphService.buildKnowledgeGraph(parseResponse);

        TestExecutionResult testExecutionResult = testExecutionService.runTests(
                repositoryContext.workspaceInfo().workspacePath(),
                repositoryContext.buildToolInfo().buildTool()
        );

        List<FailureAnalysisResult> failureAnalysisResults = List.of();
        if (testExecutionResult.failures() != null && !testExecutionResult.failures().isEmpty()) {
            var correlated = testFailureCorrelatorService.correlate(testExecutionResult.failures(), index);
            failureAnalysisResults = testFailureReviewService.reviewFailures(correlated);
        }

        StaticAnalysisReport staticAnalysisReport = staticAnalysisService.analyze(index);
        BugDetectionReport bugDetectionReport = bugDetectionService.analyze(index, staticAnalysisReport);

        BugReport bugReport = reportAggregationService.buildReport(
                repositoryContext.repositoryInfo().repositoryName(),
                staticAnalysisReport, bugDetectionReport, testExecutionResult, failureAnalysisResults
        );

        FixSuggestionReport fixSuggestionReport = fixSuggestionService.generateFixes(bugReport, index);

        VectorStoreSaveResponse vectorStoreSaveResponse = embedAndStore(index);

        log.info("Scan completed for: {}. {} issues found, {} fixes suggested, {} nodes embedded for querying.",
                scanRequest.repositoryUrl(), bugReport.summary().totalIssues(),
                fixSuggestionReport.fixesGenerated(), vectorStoreSaveResponse.totalDocumentsSaved());

        return new ScanResult(
                repositoryContext.repositoryInfo().repositoryName(),
                testExecutionResult, bugReport, fixSuggestionReport, vectorStoreSaveResponse
        );
    }

    /**
     * Never allowed to fail the whole scan — if embedding/storage has an
     * issue (e.g. Ollama unreachable), the bug report and fix suggestions
     * the user actually asked for must still come back successfully.
     * RAG querying is a bonus capability layered on top, not a prerequisite.
     */
    private VectorStoreSaveResponse embedAndStore(KnowledgeGraphIndex index) {
        try {
            EmbeddingResponse embeddingResponse = embeddingGenerationService.generate(index.getGraph());
            return vectorStoreService.save(embeddingResponse);
        } catch (Exception ex) {
            log.warn("Embedding/storage failed — scan results are still valid, but RAG querying won't work for this repo until retried: {}", ex.getMessage());
            return new VectorStoreSaveResponse(0, "unavailable");
        }
    }
}