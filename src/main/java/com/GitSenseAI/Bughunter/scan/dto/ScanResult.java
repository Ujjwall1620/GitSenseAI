package com.GitSenseAI.Bughunter.scan.dto;


import com.GitSenseAI.Bughunter.Report.dto.BugReport;
import com.GitSenseAI.Bughunter.TEST.dto.TestExecutionResult;
import com.GitSenseAI.Bughunter.fixsuggestion.dto.FixSuggestionReport;
import com.GitSenseAI.Retriever.VECTOR.dto.VectorStoreSaveResponse;

public record ScanResult(
        String repositoryName,
        TestExecutionResult testExecutionResult,
        BugReport bugReport,
        FixSuggestionReport fixSuggestionReport,
        VectorStoreSaveResponse vectorStoreSaveResponse
) {
}