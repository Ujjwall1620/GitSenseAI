package com.GitSenseAI.PARSER.Factory;

import com.GitSenseAI.PARSER.Model.Languages;
import com.GitSenseAI.PARSER.Exception.UnsupportedLanguageException;
import com.GitSenseAI.PARSER.parser.LanguageParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ParserFactory {
    private final List<LanguageParser> languageParsers;

    public LanguageParser getParser(Languages language) {
        return languageParsers.stream()
                .filter(parser -> parser.supports(language))
                .findFirst()
                .orElseThrow(() -> new UnsupportedLanguageException("No parser available for language: " + language));
    }
}