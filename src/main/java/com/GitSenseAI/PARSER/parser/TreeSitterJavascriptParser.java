package com.GitSenseAI.PARSER.parser;

import com.GitSenseAI.PARSER.DTO.ParseResult;
import com.GitSenseAI.PARSER.Model.Languages;
import com.GitSenseAI.PARSER.Exception.ParsingException;
import com.GitSenseAI.PARSER.Model.NodeType;
import com.GitSenseAI.PARSER.Model.ParsedType;
import com.GitSenseAI.PARSER.Util.FileUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.treesitter.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TreeSitterJavascriptParser implements LanguageParser {

    @Override
    public boolean supports(Languages language) {
        return language == Languages.JAVASCRIPT || language == Languages.TYPESCRIPT;
    }

    @Override
    public ParseResult parse(Path file) {
        try {
            String source = FileUtils.readContent(file);

            TSParser parser = new TSParser();
            TSLanguage jsLanguage = new TreeSitterJavascript();
            parser.setLanguage(jsLanguage);

            TSTree tree = parser.parseString(null, source);
            TSNode rootNode = tree.getRootNode();

            List<ParsedType> types = new ArrayList<>();
            collectClassDeclarations(rootNode, source, types);

            Languages detectedLanguage = FileUtils.getExtension(file).toLowerCase().startsWith("ts")
                    ? Languages.TYPESCRIPT
                    : Languages.JAVASCRIPT;

            return new ParseResult(file.toString(), detectedLanguage, "", List.of(), types);
        } catch (IOException ex) {
            log.error("Failed to read JavaScript/TypeScript file: {}", file, ex);
            throw new ParsingException("Failed to read JavaScript/TypeScript file: " + file, ex);
        } catch (Exception ex) {
            log.error("Failed to parse JavaScript/TypeScript file: {}", file, ex);
            throw new ParsingException("Failed to parse JavaScript/TypeScript file: " + file, ex);
        }
    }

    private void collectClassDeclarations(TSNode node, String source, List<ParsedType> types) {
        if ("class_declaration".equals(node.getType())) {
            TSNode nameNode = node.getChildByFieldName("name");

            if (nameNode != null) {
                String className = textOf(nameNode, source);
                int lineNumber = node.getStartPoint().getRow() + 1;

                types.add(new ParsedType(
                        className,
                        NodeType.CLASS,
                        "",
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        lineNumber
                ));
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            collectClassDeclarations(node.getChild(i), source, types);
        }
    }

    private String textOf(TSNode node, String source) {
        return source.substring((int) node.getStartByte(), (int) node.getEndByte());
    }
}