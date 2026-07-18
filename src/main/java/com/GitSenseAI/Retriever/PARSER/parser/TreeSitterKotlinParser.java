package com.GitSenseAI.Retriever.PARSER.parser;

import com.GitSenseAI.Retriever.PARSER.Model.Languages;
import org.springframework.stereotype.Component;
import org.treesitter.TSLanguage;
import org.treesitter.TreeSitterKotlin;

import java.util.Set;

@Component
public class TreeSitterKotlinParser extends AbstractTreeSitterParser {

    @Override
    public boolean supports(Languages language) {
        return language == Languages.KOTLIN;
    }

    @Override
    protected TSLanguage createLanguageBinding() {
        return new TreeSitterKotlin();
    }

    @Override
    protected Set<String> typeNodeTypes() {
        return Set.of("class_declaration");
    }

    @Override
    protected Languages targetLanguage() {
        return Languages.KOTLIN;
    }
}