package com.GitSenseAI.PARSER.parser;

import com.GitSenseAI.PARSER.Model.Languages;
import org.springframework.stereotype.Component;
import org.treesitter.TSLanguage;
import org.treesitter.TreeSitterGo;

import java.util.Set;

/**
 * Go has no class construct. The closest structural equivalent is a
 * type_spec node (used for both struct and interface type definitions),
 * which we map onto NodeType.CLASS as a pragmatic approximation for
 * graph-building purposes — not a claim that Go has classes.
 */
@Component
public class TreeSitterGoParser extends AbstractTreeSitterParser {

    @Override
    public boolean supports(Languages language) {
        return language == Languages.GO;
    }

    @Override
    protected TSLanguage createLanguageBinding() {
        return new TreeSitterGo();
    }

    @Override
    protected Set<String> typeNodeTypes() {
        return Set.of("type_spec");
    }

    @Override
    protected Languages targetLanguage() {
        return Languages.GO;
    }
}