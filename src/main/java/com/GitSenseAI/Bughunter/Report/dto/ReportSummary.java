package com.GitSenseAI.Bughunter.Report.dto;

public record ReportSummary(
        int totalIssues,
        int highCount,
        int mediumCount,
        int lowCount,
        int staticAnalysisIssues,
        int aiDetectedIssues,
        int methodsReviewedByAi,
        boolean testsPassed,
        String testExecutionStatus
) {
}