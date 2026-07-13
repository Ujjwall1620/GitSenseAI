package com.GitSenseAI.PARSER.parser;

import com.GitSenseAI.PARSER.Model.Languages;
import org.springframework.stereotype.Component;
import org.treesitter.TSLanguage;
import org.treesitter.TreeSitterRuby;

import java.util.Set;

@Component
public class TreeSitterRubyParser extends AbstractTreeSitterParser {

    @Override
    public boolean supports(Languages language) {
        return language == Languages.RUBY;
    }

    @Override
    protected TSLanguage createLanguageBinding() {
        return new TreeSitterRuby();
    }

    @Override
    protected Set<String> typeNodeTypes() {
        return Set.of("class", "module");
    }

    @Override
    protected Languages targetLanguage() {
        return Languages.RUBY;
    }
}