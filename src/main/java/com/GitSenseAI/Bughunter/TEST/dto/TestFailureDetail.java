package com.GitSenseAI.Bughunter.TEST.dto;

public record TestFailureDetail(
        String testName,
        String message,
        String exceptionType,
        String filePath,
        Integer lineNumber
) {
}