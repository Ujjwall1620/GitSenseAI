package com.GitSenseAI.Bughunter.BugDetection.service;


import com.GitSenseAI.Bughunter.BugDetection.client.BugDetectionChatClient;
import com.GitSenseAI.Bughunter.BugDetection.config.BugDetectionProperties;
import com.GitSenseAI.Bughunter.BugDetection.dto.BugDetectionReport;
import com.GitSenseAI.Bughunter.BugDetection.dto.BugFinding;
import com.GitSenseAI.Bughunter.BugDetection.dto.MethodReviewResult;
import com.GitSenseAI.Bughunter.BugDetection.util.MethodContextBuilder;
import com.GitSenseAI.Bughunter.staticanalysis.dto.Finding;
import com.GitSenseAI.Bughunter.staticanalysis.dto.StaticAnalysisReport;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.model.GraphNode;
import com.GitSenseAI.Retriever.GRAPH.model.NodeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reviews individual methods for defects the deterministic Static Analysis
 * rules structurally cannot catch (logic errors, null-safety issues, etc.).
 * Only reviews methods that actually have captured source code — currently
 * Java only, since Tree-sitter parsers for other languages don't extract
 * per-method source text yet.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BugDetectionService {

    private final MethodContextBuilder methodContextBuilder;
    private final BugDetectionChatClient bugDetectionChatClient;
    private final BugDetectionProperties bugDetectionProperties;

    public BugDetectionReport analyze(KnowledgeGraphIndex index, StaticAnalysisReport staticAnalysisReport) {
        Set<String> deadCodeNodeIds = extractDeadCodeNodeIds(staticAnalysisReport);

        List<GraphNode> candidates = selectCandidates(index, deadCodeNodeIds);
        int skippedCount = Math.toIntExact(countReviewableMethods(index) - candidates.size());

        List<MethodReviewResult> results = new ArrayList<>();
        int totalFindings = 0;

        log.info("Reviewing {} of {} eligible methods (cap: {})...",
                candidates.size(), countReviewableMethods(index), bugDetectionProperties.getMaxMethodsPerRun());

        for (GraphNode methodNode : candidates) {
            String context = methodContextBuilder.buildContext(methodNode, index);
            List<BugFinding> findings = bugDetectionChatClient.reviewMethod(context);

            totalFindings += findings.size();

            results.add(new MethodReviewResult(
                    methodNode.id(), methodNode.name(), methodNode.filePath(), methodNode.lineNumber(), findings
            ));
        }

        log.info("Bug detection completed. {} methods reviewed, {} findings.", results.size(), totalFindings);

        return new BugDetectionReport(results.size(), skippedCount, totalFindings, results);
    }

    private List<GraphNode> selectCandidates(KnowledgeGraphIndex index, Set<String> deadCodeNodeIds) {
        return index.getGraph().getNodes().stream()
                .filter(this::isReviewableMethod)
                .filter(node -> !bugDetectionProperties.isSkipDeadCode() || !deadCodeNodeIds.contains(node.id()))
                .limit(bugDetectionProperties.getMaxMethodsPerRun())
                .toList();
    }

    private boolean isReviewableMethod(GraphNode node) {
        boolean isMethodLike = node.type() == NodeType.METHOD || node.type() == NodeType.CONSTRUCTOR;
        String sourceCode = node.metadata().get("sourceCode");
        return isMethodLike && sourceCode != null && !sourceCode.isBlank();
    }

    private long countReviewableMethods(KnowledgeGraphIndex index) {
        return index.getGraph().getNodes().stream().filter(this::isReviewableMethod).count();
    }

    private Set<String> extractDeadCodeNodeIds(StaticAnalysisReport staticAnalysisReport) {
        if (staticAnalysisReport == null || staticAnalysisReport.findings() == null) {
            return Set.of();
        }

        return staticAnalysisReport.findings().stream()
                .filter(finding -> "DeadCodeRule".equals(finding.ruleName()))
                .map(Finding::nodeId)
                .collect(Collectors.toSet());
    }
}