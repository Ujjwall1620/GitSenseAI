package com.GitSenseAI.Retriever.PARSER.parser;


import com.GitSenseAI.Retriever.PARSER.DTO.ParseResult;
import com.GitSenseAI.Retriever.PARSER.Model.Languages;
import com.GitSenseAI.Retriever.PARSER.Exception.UnsupportedLanguageException;
import org.springframework.stereotype.Component;

/**
 * Alternate/experimental Java parser backed by Tree-sitter instead of JavaParser.
 * Not currently selected by ParserFactory — JavaParserAdapter remains the primary
 * Java parser per architecture requirements ("Java -> JavaParser"). This class exists
 * to satisfy the LanguageParser contract for Java via Tree-sitter if ever needed
 * (e.g. for partial/incremental parsing use cases JavaParser doesn't support well).
 */
@Component
public class TreeSitterJavaParser implements LanguageParser {

    @Override
    public boolean supports(Languages language) {
        return false;
    }

    @Override
    public ParseResult parse(java.nio.file.Path file) {
        throw new UnsupportedLanguageException(
                "TreeSitterJavaParser is not active. JavaParserAdapter handles Java files."
        );
    }
}