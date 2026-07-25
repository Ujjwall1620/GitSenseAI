package com.GitSenseAI.Bughunter.Report.dto;

import java.util.List;
import java.util.Map;

public record BugReport(
        String repositoryName,
        ReportSummary summary,
        List<BugReportItem> issues,
        Map<String, List<BugReportItem>> issuesByFile
) {
}