package com.GitSenseAI.PARSER.parser;

import com.GitSenseAI.PARSER.Model.Languages;
import org.springframework.stereotype.Component;
import org.treesitter.TSLanguage;
import org.treesitter.TreeSitterC;

import java.util.Set;

/**
 * C has no class construct. struct_specifier is the closest structural
 * equivalent and is mapped onto NodeType.CLASS as a pragmatic
 * approximation for graph-building purposes only.
 */
@Component
public class TreeSitterCParser extends AbstractTreeSitterParser {

    @Override
    public boolean supports(Languages language) {
        return language == Languages.C;
    }

    @Override
    protected TSLanguage createLanguageBinding() {
        return new TreeSitterC();
    }

    @Override
    protected Set<String> typeNodeTypes() {
        return Set.of("struct_specifier");
    }

    @Override
    protected Languages targetLanguage() {
        return Languages.C;
    }
}