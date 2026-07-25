package com.GitSenseAI.Bughunter.fixsuggestion.dto;

import java.util.List;

public record FixSuggestionReport(
        int totalIssuesConsidered,
        int fixesGenerated,
        int skipped,
        List<FixSuggestionResult> results
) {
}