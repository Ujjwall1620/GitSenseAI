package com.GitSenseAI.Bughunter.failurecorrelation.dto;


public record CorrelatedFailure(
        String testName,
        String exceptionType,
        String message,
        String matchedNodeId,
        String matchedMethodName,
        String matchedFilePath,
        Integer matchedLineNumber,
        String methodSourceCode,
        boolean matched
) {
}