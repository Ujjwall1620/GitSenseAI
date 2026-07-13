package com.GitSenseAI.PARSER.Scanner;

import com.GitSenseAI.PARSER.Config.ParserProperties;
import com.GitSenseAI.PARSER.Exception.FileScanException;
import com.GitSenseAI.PARSER.Util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParserFileScanner {

    private final ParserProperties parserProperties;

    public List<Path> scan(Path workspaceRoot) {
        log.info("Scanning workspace...");

        try (Stream<Path> pathStream = Files.walk(workspaceRoot)) {
            List<Path> files = pathStream
                    .filter(Files::isRegularFile)
                    .filter(this::isNotIgnored)
                    .filter(this::isSupportedExtension)
                    .filter(this::isWithinSizeLimit)
                    .filter(path -> !FileUtils.isBinary(path))
                    .limit(parserProperties.getMaxFiles())
                    .collect(Collectors.toList());

            log.info("Found {} files.", files.size());

            return files;
        } catch (IOException ex) {
            log.error("Failed to scan workspace: {}", workspaceRoot, ex);
            throw new FileScanException("Failed to scan workspace: " + workspaceRoot, ex);
        }
    }

    private boolean isNotIgnored(Path path) {
        for (Path part : path) {
            String name = part.toString();

            if (parserProperties.getIgnoredDirectories().contains(name)) {
                return false;
            }

            if (name.startsWith(".") && !name.equals(".")) {
                return false;
            }
        }

        return true;
    }

    private boolean isSupportedExtension(Path path) {
        String extension = FileUtils.getExtension(path).toLowerCase();
        return parserProperties.getSupportedExtensions().contains(extension);
    }

    private boolean isWithinSizeLimit(Path path) {
        try {
            return Files.size(path) <= parserProperties.getMaxFileSizeBytes();
        } catch (IOException ex) {
            log.warn("Could not read size for file: {}", path, ex);
            return false;
        }
    }
}