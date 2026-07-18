package com.GitSenseAI.Retriever.REPOSITORY.Util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RespositoryFileScanner {

    public Map<String, Long> countFilesByExtension(Path rootPath) throws IOException {
        try (Stream<Path> paths = Files.walk(rootPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(this::extractExtension)
                    .filter(extension -> !extension.isBlank())
                    .collect(Collectors.groupingBy(extension -> extension, Collectors.counting()));
        }
    }

    public long countTotalFiles(Path rootPath) throws IOException {
        try (Stream<Path> paths = Files.walk(rootPath)) {
            return paths.filter(Files::isRegularFile).count();
        }
    }

    public long countTotalLines(Path rootPath, List<String> textExtensions) throws IOException {
        AtomicLong totalLines = new AtomicLong(0);

        try (Stream<Path> paths = Files.walk(rootPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> textExtensions.contains(extractExtension(path)))
                    .forEach(path -> totalLines.addAndGet(countLinesSafely(path)));
        }

        return totalLines.get();
    }

    public boolean fileExists(Path rootPath, String fileName) {
        return Files.exists(rootPath.resolve(fileName));
    }

    /** Checks for any file with the given extension, shallow (depth 2) so detection stays fast even on large repos. */
    public boolean anyFileWithExtension(Path rootPath, String extension) throws IOException {
        try (Stream<Path> paths = Files.walk(rootPath, 2)) {
            return paths
                    .filter(Files::isRegularFile)
                    .anyMatch(path -> extractExtension(path).equalsIgnoreCase(extension));
        }
    }

    private String extractExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1).toLowerCase();
    }

    private long countLinesSafely(Path path) {
        try (Stream<String> lines = Files.lines(path)) {
            return lines.count();
        } catch (IOException | UncheckedIOException ex) {
            return 0L;
        }
    }
}