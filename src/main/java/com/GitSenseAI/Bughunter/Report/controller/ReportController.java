package com.GitSenseAI.Bughunter.Report.controller;


import com.GitSenseAI.Bughunter.BugDetection.dto.BugDetectionReport;
import com.GitSenseAI.Bughunter.BugDetection.service.BugDetectionService;
import com.GitSenseAI.Bughunter.Report.dto.BugReport;
import com.GitSenseAI.Bughunter.Report.service.ReportAggregationService;
import com.GitSenseAI.Bughunter.TEST.dto.TestExecutionResult;
import com.GitSenseAI.Bughunter.TEST.service.TestExecutionService;
import com.GitSenseAI.Bughunter.staticanalysis.dto.StaticAnalysisReport;
import com.GitSenseAI.Bughunter.staticanalysis.service.StaticAnalysisService;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.service.KnowledgeGraphService;
import com.GitSenseAI.Retriever.PARSER.DTO.ParseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController {

    private final KnowledgeGraphService knowledgeGraphService;
    private final StaticAnalysisService staticAnalysisService;
    private final BugDetectionService bugDetectionService;
    private final TestExecutionService testExecutionService;
    private final ReportAggregationService reportAggregationService;

    @PostMapping("/generate")
    public ResponseEntity<BugReport> generate(@RequestBody ParseResponse parseResponse,
                                              @RequestParam(required = false) String repositoryName,
                                              @RequestParam(required = false) String workspacePath,
                                              @RequestParam(required = false) com.GitSenseAI.Retriever.REPOSITORY.Entity.enums.BuildTools buildTool) {

        log.info("Generating full bug report for [{}]...", repositoryName);

        KnowledgeGraphIndex index = knowledgeGraphService.buildKnowledgeGraph(parseResponse);
        StaticAnalysisReport staticAnalysisReport = staticAnalysisService.analyze(index);
        BugDetectionReport bugDetectionReport = bugDetectionService.analyze(index, staticAnalysisReport);

        TestExecutionResult testExecutionResult = (workspacePath != null && buildTool != null)
                ? testExecutionService.runTests(workspacePath, buildTool)
                : null;

        BugReport report = reportAggregationService.buildReport(
                repositoryName != null ? repositoryName : "unknown",
                staticAnalysisReport, bugDetectionReport, testExecutionResult, null
        );

        return ResponseEntity.ok(report);
    }
}