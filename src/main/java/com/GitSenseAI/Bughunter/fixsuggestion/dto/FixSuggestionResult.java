package com.GitSenseAI.Bughunter.fixsuggestion.dto;


import com.GitSenseAI.Bughunter.Report.dto.BugReportItem;

public record FixSuggestionResult(
        BugReportItem issue,
        CodeFix fix,
        boolean fixGenerated,
        String skipReason
) {
}