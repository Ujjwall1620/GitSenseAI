package com.GitSenseAI.Bughunter.failurecorrelation.dto;

import com.GitSenseAI.Bughunter.TEST.dto.TestExecutionResult;
import com.GitSenseAI.Retriever.PARSER.DTO.ParseResponse;

public record FailureAnalysisRequest(
        ParseResponse parseResponse,
        TestExecutionResult testExecutionResult
) {
}