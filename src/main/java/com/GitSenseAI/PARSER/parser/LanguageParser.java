package com.GitSenseAI.PARSER.parser;



import com.GitSenseAI.PARSER.DTO.ParseResult;
import com.GitSenseAI.PARSER.Model.Languages;

import java.nio.file.Path;

public interface LanguageParser {

    boolean supports(Languages language);

    ParseResult parse(Path file);
}