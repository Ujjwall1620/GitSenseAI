package com.GitSenseAI.PARSER.Service;

import com.GitSenseAI.PARSER.DTO.ParseRequest;
import com.GitSenseAI.PARSER.DTO.ParseResponse;
import com.GitSenseAI.PARSER.DTO.ParseResult;
import com.GitSenseAI.PARSER.DTO.ProjectGraphResponse;
import com.GitSenseAI.PARSER.Exception.ParsingException;
import com.GitSenseAI.PARSER.Exception.UnsupportedLanguageException;
import com.GitSenseAI.PARSER.Model.Languages;
import com.GitSenseAI.PARSER.Factory.ParserFactory;
import com.GitSenseAI.PARSER.Graph.ProjectGraph;
import com.GitSenseAI.PARSER.Graph.ProjectGraphBuilder;
import com.GitSenseAI.PARSER.Model.ParsedMethod;
import com.GitSenseAI.PARSER.Model.ParsedType;
import com.GitSenseAI.PARSER.Scanner.ParserFileScanner;
import com.GitSenseAI.PARSER.Symbol.Symbol;
import com.GitSenseAI.PARSER.Symbol.SymbolTable;
import com.GitSenseAI.PARSER.Util.FileUtils;
import com.GitSenseAI.PARSER.parser.LanguageParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParserService {

    private final ParserFileScanner fileScanner;
    private final ParserFactory parserFactory;
    private final ProjectGraphBuilder projectGraphBuilder;

    public ParseResponse parseRepository(ParseRequest parseRequest) {
        Path workspacePath = Path.of(parseRequest.repositoryContext().workspaceInfo().workspacePath());

        List<Path> files = fileScanner.scan(workspacePath);

        List<ParseResult> parseResults = new ArrayList<>();
        List<String> parseErrors = new ArrayList<>();
        SymbolTable symbolTable = new SymbolTable();

        log.info("Parsing files...");

        for (Path file : files) {
            parseFile(file, parseResults, parseErrors, symbolTable);
        }

        log.info("Building project graph...");
        ProjectGraph projectGraph = projectGraphBuilder.build(parseResults, symbolTable);

        log.info("Parser completed.");

        return new ParseResponse(
                files.size(),
                parseResults.size(),
                parseErrors,
                new ProjectGraphResponse(projectGraph.getNodes(), projectGraph.getEdges())
        );
    }

    private void parseFile(Path file, List<ParseResult> parseResults, List<String> parseErrors, SymbolTable symbolTable) {
        Languages language = Languages.fromExtension(FileUtils.getExtension(file));

        if (language == Languages.UNSUPPORTED) {
            return;
        }

        try {
            LanguageParser parser = parserFactory.getParser(language);
            ParseResult result = parser.parse(file);

            parseResults.add(result);
            registerSymbols(symbolTable, result);
        } catch (UnsupportedLanguageException ex) {
            log.warn("Skipping file with unsupported language: {}", file, ex);
        } catch (ParsingException ex) {
            log.error("Failed to parse file: {}", file, ex);
            parseErrors.add(file + ": " + ex.getMessage());
        }
    }

    private void registerSymbols(SymbolTable symbolTable, ParseResult result) {
        for (ParsedType type : result.types()) {
            symbolTable.addSymbol(new Symbol(type.name(), type.nodeType().name(), result.filePath(), type.lineNumber()));

            for (ParsedMethod method : type.methods()) {
                symbolTable.addSymbol(new Symbol(method.name(), "METHOD", result.filePath(), method.lineNumber()));
            }
        }
    }
}