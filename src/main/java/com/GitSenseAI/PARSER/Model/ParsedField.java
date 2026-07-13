package com.GitSenseAI.PARSER.Model;

import java.util.List;

public record ParsedField(
        String name,
        String type,
        List<String> annotations,
        int lineNumber
) {
}