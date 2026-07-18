package com.GitSenseAI.Bughunter.TEST.dto;


import com.GitSenseAI.Bughunter.TEST.enums.TestExecutionStatus;

import java.util.List;

public record TestExecutionResult(
        TestExecutionStatus status,
        String summary,
        int totalTests,
        int passedTests,
        int failedTests,
        List<TestFailureDetail> failures,
        List<String> rawOutputTail
) {
    public static TestExecutionResult skipped(String reason) {
        return new TestExecutionResult(TestExecutionStatus.SKIPPED, reason, 0, 0, 0, List.of(), List.of());
    }

    public static TestExecutionResult error(String message) {
        return new TestExecutionResult(TestExecutionStatus.ERROR, message, 0, 0, 0, List.of(), List.of());
    }
}