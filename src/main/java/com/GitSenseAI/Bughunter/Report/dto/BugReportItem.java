package com.GitSenseAI.Bughunter.Report.dto;


import com.GitSenseAI.Bughunter.Report.Model.Severity;

public record BugReportItem(
        String nodeId,
        String source,
        String category,
        Severity severity,
        String nodeName,
        String nodeType,
        String filePath,
        int lineNumber,
        String description,
        String suggestedFix
) {
}