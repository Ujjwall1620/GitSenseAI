package com.GitSenseAI.Retriever.PARSER.DTO;

import java.util.List;

public record ParseResponse(
        int totalFilesScanned,
        int totalFilesParsed,
        List<String> parseErrors,
        List<ParseResult> parseResults
) {
}