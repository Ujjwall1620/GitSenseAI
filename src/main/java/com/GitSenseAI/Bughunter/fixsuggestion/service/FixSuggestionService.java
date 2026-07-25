package com.GitSenseAI.Bughunter.fixsuggestion.service;

import com.GitSenseAI.Bughunter.Report.dto.BugReport;
import com.GitSenseAI.Bughunter.Report.dto.BugReportItem;
import com.GitSenseAI.Bughunter.fixsuggestion.client.FixSuggestionChatClient;
import com.GitSenseAI.Bughunter.fixsuggestion.config.FixSuggestionProperties;
import com.GitSenseAI.Bughunter.fixsuggestion.dto.AiFixResponse;
import com.GitSenseAI.Bughunter.fixsuggestion.dto.CodeFix;
import com.GitSenseAI.Bughunter.fixsuggestion.dto.FixSuggestionReport;
import com.GitSenseAI.Bughunter.fixsuggestion.dto.FixSuggestionResult;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.model.GraphNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Generates an actual corrected-code suggestion for each reported issue,
 * by re-locating the original method in the Knowledge Graph and asking
 * the chat model for a fixed version. Only issues pointing at a resolvable
 * method with captured source code (currently Java only) can be fixed —
 * everything else is explicitly marked skipped, with a reason, rather than
 * silently dropped.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FixSuggestionService {

    private final FixSuggestionChatClient fixSuggestionChatClient;
    private final FixSuggestionProperties fixSuggestionProperties;

    public FixSuggestionReport generateFixes(BugReport bugReport, KnowledgeGraphIndex index) {
        List<BugReportItem> candidates = bugReport.issues().stream()
                .filter(item -> item.nodeId() != null)
                .limit(fixSuggestionProperties.getMaxFixesPerRun())
                .toList();

        log.info("Generating fixes for {} of {} reported issues...", candidates.size(), bugReport.issues().size());

        List<FixSuggestionResult> results = new ArrayList<>();

        for (BugReportItem item : candidates) {
            results.add(generateFixFor(item, index));
        }

        int generated = (int) results.stream().filter(FixSuggestionResult::fixGenerated).count();

        log.info("Fix generation completed. {} fixes generated, {} skipped.", generated, results.size() - generated);

        return new FixSuggestionReport(bugReport.issues().size(), generated, results.size() - generated, results);
    }

    private FixSuggestionResult generateFixFor(BugReportItem item, KnowledgeGraphIndex index) {
        Optional<GraphNode> node = index.findNode(item.nodeId());

        if (node.isEmpty()) {
            return skipped(item, "Referenced graph node no longer found");
        }

        String sourceCode = node.get().metadata().get("sourceCode");

        if (sourceCode == null || sourceCode.isBlank()) {
            return skipped(item, "No source code captured for this node (non-Java or non-method finding)");
        }

        AiFixResponse aiFix = fixSuggestionChatClient.generateFix(sourceCode, item.description());

        if (aiFix == null || aiFix.fixedCode() == null || aiFix.fixedCode().isBlank()) {
            return skipped(item, "AI did not return a usable fix");
        }

        CodeFix fix = new CodeFix(sourceCode, aiFix.fixedCode(), aiFix.explanation());

        return new FixSuggestionResult(item, fix, true, null);
    }

    private FixSuggestionResult skipped(BugReportItem item, String reason) {
        return new FixSuggestionResult(item, null, false, reason);
    }
}