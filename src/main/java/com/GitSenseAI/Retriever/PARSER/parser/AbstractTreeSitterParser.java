package com.GitSenseAI.Retriever.PARSER.parser;

import com.GitSenseAI.Retriever.PARSER.DTO.ParseResult;
import com.GitSenseAI.Retriever.PARSER.Exception.ParsingException;
import com.GitSenseAI.Retriever.PARSER.Model.Languages;
import com.GitSenseAI.Retriever.PARSER.Model.NodeType;
import com.GitSenseAI.Retriever.PARSER.Model.ParsedType;
import com.GitSenseAI.Retriever.PARSER.Util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.treesitter.TSLanguage;
import org.treesitter.TSNode;
import org.treesitter.TSParser;
import org.treesitter.TSTree;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Shared traversal logic for every Tree-sitter-backed LanguageParser.
 * Node type strings are grammar-specific — verify against the target
 * grammar's node-types.json if extraction returns unexpectedly empty.
 */
@Slf4j
public abstract class AbstractTreeSitterParser implements LanguageParser {

    protected abstract TSLanguage createLanguageBinding();

    protected abstract Set<String> typeNodeTypes();

    protected abstract Languages targetLanguage();

    @Override
    public ParseResult parse(Path file) {
        try {
            String source = FileUtils.readContent(file);

            TSParser parser = new TSParser();
            parser.setLanguage(createLanguageBinding());

            TSTree tree = parser.parseString(null, source);
            TSNode rootNode = tree.getRootNode();

            List<ParsedType> types = new ArrayList<>();
            collectTypes(rootNode, source, types);

            return new ParseResult(file.toString(), targetLanguage(), "", List.of(), types);
        } catch (IOException ex) {
            log.error("Failed to read file: {}", file, ex);
            throw new ParsingException("Failed to read file: " + file, ex);
        } catch (Exception ex) {
            log.error("Failed to parse file [{}] as {}", file, targetLanguage(), ex);
            throw new ParsingException("Failed to parse file: " + file, ex);
        }
    }

    private void collectTypes(TSNode node, String source, List<ParsedType> types) {
        if (typeNodeTypes().contains(node.getType())) {
            TSNode nameNode = node.getChildByFieldName("name");

            if (nameNode != null) {
                String name = source.substring((int) nameNode.getStartByte(), (int) nameNode.getEndByte());
                int lineNumber = node.getStartPoint().getRow() + 1;

                types.add(new ParsedType(
                        name, NodeType.CLASS, "", List.of(), List.of(), List.of(), List.of(), List.of(), lineNumber
                ));
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            collectTypes(node.getChild(i), source, types);
        }
    }
}