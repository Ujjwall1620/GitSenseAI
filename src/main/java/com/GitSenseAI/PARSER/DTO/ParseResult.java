package com.GitSenseAI.PARSER.DTO;



import com.GitSenseAI.PARSER.Model.Languages;
import com.GitSenseAI.PARSER.Model.ParsedType;

import java.util.List;

public record ParseResult(
        String filePath,
        Languages language,
        String packageName,
        List<String> imports,
        List<ParsedType> types
) {
}