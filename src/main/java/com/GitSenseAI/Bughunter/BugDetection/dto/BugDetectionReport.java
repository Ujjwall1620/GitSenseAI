package com.GitSenseAI.Bughunter.BugDetection.dto;

import java.util.List;

public record BugDetectionReport(
        int totalMethodsReviewed,
        int totalMethodsSkipped,
        int totalFindings,
        List<MethodReviewResult> results
) {
}