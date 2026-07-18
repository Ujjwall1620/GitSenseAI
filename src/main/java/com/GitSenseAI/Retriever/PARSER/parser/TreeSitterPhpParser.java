package com.GitSenseAI.Retriever.PARSER.parser;

import com.GitSenseAI.Retriever.PARSER.Model.Languages;
import org.springframework.stereotype.Component;
import org.treesitter.TSLanguage;
import org.treesitter.TreeSitterPhp;

import java.util.Set;

@Component
public class TreeSitterPhpParser extends AbstractTreeSitterParser {

    @Override
    public boolean supports(Languages language) {
        return language == Languages.PHP;
    }

    @Override
    protected TSLanguage createLanguageBinding() {
        return new TreeSitterPhp();
    }

    @Override
    protected Set<String> typeNodeTypes() {
        return Set.of("class_declaration", "interface_declaration");
    }

    @Override
    protected Languages targetLanguage() {
        return Languages.PHP;
    }
}
