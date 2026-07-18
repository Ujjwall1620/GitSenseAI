package com.GitSenseAI.Bughunter.staticanalysis.dto;


import com.GitSenseAI.Bughunter.staticanalysis.model.Severity;

public record Finding(
        String ruleName,
        String nodeId,
        String nodeName,
        String nodeType,
        Severity severity,
        String message,
        String filePath,
        int lineNumber
) {
}