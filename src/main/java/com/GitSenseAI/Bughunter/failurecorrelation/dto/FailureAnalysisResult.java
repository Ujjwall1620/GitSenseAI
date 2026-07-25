package com.GitSenseAI.Bughunter.failurecorrelation.dto;


import com.GitSenseAI.Bughunter.BugDetection.dto.BugFinding;

import java.util.List;

public record FailureAnalysisResult(
        CorrelatedFailure failure,
        List<BugFinding> aiFindings
) {
}