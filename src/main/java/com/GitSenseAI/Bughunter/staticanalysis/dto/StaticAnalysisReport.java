package com.GitSenseAI.Bughunter.staticanalysis.dto;

import java.util.List;

public record StaticAnalysisReport(
        int totalFindings,
        int highCount,
        int mediumCount,
        int lowCount,
        List<Finding> findings
) {
}