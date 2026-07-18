package com.GitSenseAI.Bughunter.staticanalysis.service;

import com.GitSenseAI.Bughunter.staticanalysis.dto.Finding;
import com.GitSenseAI.Bughunter.staticanalysis.dto.StaticAnalysisReport;
import com.GitSenseAI.Bughunter.staticanalysis.model.Severity;
import com.GitSenseAI.Bughunter.staticanalysis.rule.StaticAnalysisRule;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Runs every registered StaticAnalysisRule against the Knowledge Graph and
 * aggregates the results. A single failing rule is logged and skipped —
 * it never blocks the other rules or the caller, same isolation philosophy
 * as Test Execution Module.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StaticAnalysisService {

    private final List<StaticAnalysisRule> rules;

    public StaticAnalysisReport analyze(KnowledgeGraphIndex index) {
        List<Finding> allFindings = new ArrayList<>();

        for (StaticAnalysisRule rule : rules) {
            try {
                log.info("Running static analysis rule: {}", rule.getName());
                List<Finding> findings = rule.analyze(index);
                log.info("Rule {} produced {} findings.", rule.getName(), findings.size());
                allFindings.addAll(findings);
            } catch (Exception ex) {
                log.error("Static analysis rule {} failed — skipping its findings", rule.getName(), ex);
            }
        }

        Map<Severity, Long> countBySeverity = allFindings.stream()
                .collect(Collectors.groupingBy(Finding::severity, Collectors.counting()));

        return new StaticAnalysisReport(
                allFindings.size(),
                countBySeverity.getOrDefault(Severity.HIGH, 0L).intValue(),
                countBySeverity.getOrDefault(Severity.MEDIUM, 0L).intValue(),
                countBySeverity.getOrDefault(Severity.LOW, 0L).intValue(),
                allFindings
        );
    }
}