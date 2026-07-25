package com.GitSenseAI.Bughunter.fixsuggestion.dto;


import com.GitSenseAI.Bughunter.Report.dto.BugReport;
import com.GitSenseAI.Retriever.PARSER.DTO.ParseResponse;

public record FixSuggestionRequest(
        ParseResponse parseResponse,
        BugReport bugReport
) {
}