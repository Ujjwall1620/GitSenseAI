package com.GitSenseAI.Bughunter.Report.service;

import com.GitSenseAI.Bughunter.BugDetection.dto.BugDetectionReport;
import com.GitSenseAI.Bughunter.Report.Model.Severity;
import com.GitSenseAI.Bughunter.Report.dto.BugReport;
import com.GitSenseAI.Bughunter.Report.dto.BugReportItem;
import com.GitSenseAI.Bughunter.Report.dto.ReportSummary;
import com.GitSenseAI.Bughunter.Report.mapper.AiFindingMapper;
import com.GitSenseAI.Bughunter.Report.mapper.FailureFindingMapper;
import com.GitSenseAI.Bughunter.Report.mapper.StaticFindingMapper;
import com.GitSenseAI.Bughunter.TEST.dto.TestExecutionResult;
import com.GitSenseAI.Bughunter.TEST.enums.TestExecutionStatus;
import com.GitSenseAI.Bughunter.failurecorrelation.dto.FailureAnalysisResult;
import com.GitSenseAI.Bughunter.staticanalysis.dto.StaticAnalysisReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportAggregationService {

    private final StaticFindingMapper staticFindingMapper;
    private final AiFindingMapper aiFindingMapper;
    private final FailureFindingMapper failureFindingMapper;

    public BugReport buildReport(String repositoryName,
                                 StaticAnalysisReport staticAnalysisReport,
                                 BugDetectionReport bugDetectionReport,
                                 TestExecutionResult testExecutionResult,
                                 List<FailureAnalysisResult> failureAnalysisResults) {

        List<BugReportItem> issues = new ArrayList<>();

        if (staticAnalysisReport != null && staticAnalysisReport.findings() != null) {
            staticAnalysisReport.findings().forEach(finding -> issues.add(staticFindingMapper.toReportItem(finding)));
        }

        if (bugDetectionReport != null && bugDetectionReport.results() != null) {
            bugDetectionReport.results().forEach(result -> issues.addAll(aiFindingMapper.toReportItems(result)));
        }

        if (failureAnalysisResults != null) {
            failureAnalysisResults.forEach(result -> issues.addAll(failureFindingMapper.toReportItems(result)));
        }

        issues.sort(Comparator.comparing(BugReportItem::severity).reversed());

        Map<String, List<BugReportItem>> issuesByFile = issues.stream()
                .collect(Collectors.groupingBy(item -> item.filePath() != null ? item.filePath() : "unknown"));

        ReportSummary summary = buildSummary(issues, staticAnalysisReport, bugDetectionReport, testExecutionResult);

        log.info("Aggregated report for [{}]: {} total issues ({} high, {} medium, {} low).",
                repositoryName, summary.totalIssues(), summary.highCount(), summary.mediumCount(), summary.lowCount());

        return new BugReport(repositoryName, summary, issues, issuesByFile);
    }

    private ReportSummary buildSummary(List<BugReportItem> issues,
                                       StaticAnalysisReport staticAnalysisReport,
                                       BugDetectionReport bugDetectionReport,
                                       TestExecutionResult testExecutionResult) {

        Map<Severity, Long> countBySeverity = issues.stream()
                .collect(Collectors.groupingBy(BugReportItem::severity, Collectors.counting()));

        int staticCount = staticAnalysisReport != null ? staticAnalysisReport.totalFindings() : 0;
        int aiCount = bugDetectionReport != null ? bugDetectionReport.totalFindings() : 0;
        int methodsReviewed = bugDetectionReport != null ? bugDetectionReport.totalMethodsReviewed() : 0;

        boolean testsPassed = testExecutionResult != null && testExecutionResult.status() == TestExecutionStatus.PASSED;
        String testStatus = testExecutionResult != null ? testExecutionResult.status().name() : "NOT_RUN";

        return new ReportSummary(
                issues.size(),
                countBySeverity.getOrDefault(Severity.HIGH, 0L).intValue(),
                countBySeverity.getOrDefault(Severity.MEDIUM, 0L).intValue(),
                countBySeverity.getOrDefault(Severity.LOW, 0L).intValue(),
                staticCount, aiCount, methodsReviewed, testsPassed, testStatus
        );
    }
}