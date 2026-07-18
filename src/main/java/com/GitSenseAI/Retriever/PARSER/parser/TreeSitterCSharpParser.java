package com.GitSenseAI.Retriever.PARSER.parser;

import com.GitSenseAI.Retriever.PARSER.Model.Languages;
import org.springframework.stereotype.Component;
import org.treesitter.TSLanguage;
import org.treesitter.TreeSitterCSharp;

import java.util.Set;

@Component
public class TreeSitterCSharpParser extends AbstractTreeSitterParser {

    @Override
    public boolean supports(Languages language) {
        return language == Languages.CSHARP;
    }

    @Override
    protected TSLanguage createLanguageBinding() {
        return new TreeSitterCSharp();
    }

    @Override
    protected Set<String> typeNodeTypes() {
        return Set.of("class_declaration", "interface_declaration", "struct_declaration");
    }

    @Override
    protected Languages targetLanguage() {
        return Languages.CSHARP;
    }
}