package com.GitSenseAI.Bughunter.BugDetection.dto;


import com.GitSenseAI.Bughunter.staticanalysis.model.Severity;

public record BugFinding(
        String category,
        Severity severity,
        String description,
        String suggestedFix
) {
}