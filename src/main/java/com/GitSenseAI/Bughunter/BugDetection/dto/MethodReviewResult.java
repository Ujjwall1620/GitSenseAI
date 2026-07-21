package com.GitSenseAI.Bughunter.BugDetection.dto;


import java.util.List;

public record MethodReviewResult(
        String nodeId,
        String methodName,
        String filePath,
        int lineNumber,
        List<BugFinding> findings
) {
}