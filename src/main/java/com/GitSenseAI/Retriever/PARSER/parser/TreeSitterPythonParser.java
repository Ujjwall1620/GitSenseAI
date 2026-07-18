package com.GitSenseAI.Retriever.PARSER.parser;


import com.GitSenseAI.Retriever.PARSER.Model.Languages;
import org.springframework.stereotype.Component;
import org.treesitter.TSLanguage;
import org.treesitter.TreeSitterPython;

import java.util.Set;

@Component
public class TreeSitterPythonParser extends AbstractTreeSitterParser {

    @Override
    public boolean supports(Languages language) {
        return language == Languages.PYTHON;
    }

    @Override
    protected TSLanguage createLanguageBinding() {
        return new TreeSitterPython();
    }

    @Override
    protected Set<String> typeNodeTypes() {
        return Set.of("class_definition");
    }

    @Override
    protected Languages targetLanguage() {
        return Languages.PYTHON;
    }
}