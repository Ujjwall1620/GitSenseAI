package com.GitSenseAI.PARSER.parser;


import com.GitSenseAI.PARSER.Model.Languages;
import org.springframework.stereotype.Component;
import org.treesitter.TSLanguage;
import org.treesitter.TreeSitterJavascript;

import java.util.Set;

@Component
public class TreeSitterJavascriptParser extends AbstractTreeSitterParser {

    @Override
    public boolean supports(Languages language) {
        return language == Languages.JAVASCRIPT;
    }

    @Override
    protected TSLanguage createLanguageBinding() {
        return new TreeSitterJavascript();
    }

    @Override
    protected Set<String> typeNodeTypes() {
        return Set.of("class_declaration");
    }

    @Override
    protected Languages targetLanguage() {
        return Languages.JAVASCRIPT;
    }
}