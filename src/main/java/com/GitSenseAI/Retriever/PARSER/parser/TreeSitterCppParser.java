package com.GitSenseAI.Retriever.PARSER.parser;

import com.GitSenseAI.Retriever.PARSER.Model.Languages;
import org.springframework.stereotype.Component;
import org.treesitter.TSLanguage;
import org.treesitter.TreeSitterCpp;

import java.util.Set;

@Component
public class TreeSitterCppParser extends AbstractTreeSitterParser {

    @Override
    public boolean supports(Languages language) {
        return language == Languages.CPP;
    }

    @Override
    protected TSLanguage createLanguageBinding() {
        return new TreeSitterCpp();
    }

    @Override
    protected Set<String> typeNodeTypes() {
        return Set.of("class_specifier", "struct_specifier");
    }

    @Override
    protected Languages targetLanguage() {
        return Languages.CPP;
    }
}