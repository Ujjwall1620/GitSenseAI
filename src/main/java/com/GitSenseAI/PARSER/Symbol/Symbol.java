package com.GitSenseAI.PARSER.Symbol;

public record Symbol(
        String name,
        String kind,
        String filePath,
        int lineNumber
) {
}