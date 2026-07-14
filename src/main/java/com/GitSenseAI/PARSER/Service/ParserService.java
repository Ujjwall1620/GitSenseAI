package com.GitSenseAI.PARSER.Service;


import com.GitSenseAI.PARSER.DTO.ParseRequest;
import com.GitSenseAI.PARSER.DTO.ParseResponse;
import com.GitSenseAI.PARSER.DTO.ParseResult;
import com.GitSenseAI.PARSER.Exception.ParsingException;
import com.GitSenseAI.PARSER.Exception.UnsupportedLanguageException;
import com.GitSenseAI.PARSER.Factory.ParserFactory;
import com.GitSenseAI.PARSER.Model.Languages;
import com.GitSenseAI.PARSER.Scanner.ParserFileScanner;
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

    public ParseResponse parseRepository(ParseRequest parseRequest) {
        Path workspacePath = Path.of(parseRequest.repositoryContext().workspaceInfo().workspacePath());

        List<Path> files = fileScanner.scan(workspacePath);

        List<ParseResult> parseResults = new ArrayList<>();
        List<String> parseErrors = new ArrayList<>();

        log.info("Parsing files...");

        for (Path file : files) {
            parseFile(file, parseResults, parseErrors);
        }

        log.info("Parser completed. {} files parsed successfully, {} errors.", parseResults.size(), parseErrors.size());

        return new ParseResponse(files.size(), parseResults.size(), parseErrors, parseResults);
    }

    private void parseFile(Path file, List<ParseResult> parseResults, List<String> parseErrors) {
        Languages language = Languages.fromExtension(FileUtils.getExtension(file));

        if (language == Languages.UNSUPPORTED) {
            return;
        }

        try {
            LanguageParser parser = parserFactory.getParser(language);
            parseResults.add(parser.parse(file));
        } catch (UnsupportedLanguageException ex) {
            log.warn("Skipping file with unsupported language: {}", file, ex);
        } catch (ParsingException ex) {
            log.error("Failed to parse file: {}", file, ex);
            parseErrors.add(file + ": " + ex.getMessage());
        }
    }
}