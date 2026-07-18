package com.GitSenseAI.Retriever.PARSER.parser;



import com.GitSenseAI.Retriever.PARSER.DTO.ParseResult;
import com.GitSenseAI.Retriever.PARSER.Model.Languages;

import java.nio.file.Path;

public interface LanguageParser {

    boolean supports(Languages language);

    ParseResult parse(Path file);
}