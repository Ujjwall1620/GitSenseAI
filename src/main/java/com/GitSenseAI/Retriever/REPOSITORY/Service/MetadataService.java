package com.GitSenseAI.Retriever.REPOSITORY.Service;

import com.GitSenseAI.Retriever.REPOSITORY.DTO.RepositoryMetadata;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.WorkspaceInfo;
import com.GitSenseAI.Retriever.REPOSITORY.Util.RespositoryFileScanner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataService {

    private static final List<String> TEXT_EXTENSIONS = List.of(
            "java", "kt", "py", "js", "ts", "go", "c", "cpp", "cs", "rb", "php", "xml", "yml", "yaml", "json"
    );

    private final RespositoryFileScanner fileScanner;

    public RepositoryMetadata readMetadata(WorkspaceInfo workspaceInfo) {
        Path rootPath = Path.of(workspaceInfo.workspacePath());

        long totalFiles = countTotalFiles(rootPath);
        long totalLinesOfCode = countTotalLines(rootPath);

        File pomFile = rootPath.resolve("pom.xml").toFile();

        if (pomFile.exists()) {
            return readMavenMetadata(pomFile, totalFiles, totalLinesOfCode);
        }

        return defaultMetadata(rootPath, totalFiles, totalLinesOfCode);
    }

    private long countTotalFiles(Path rootPath) {
        try {
            return fileScanner.countTotalFiles(rootPath);
        } catch (IOException ex) {
            log.warn("Failed to count total files in workspace [{}]", rootPath, ex);
            return 0L;
        }
    }

    private long countTotalLines(Path rootPath) {
        try {
            return fileScanner.countTotalLines(rootPath, TEXT_EXTENSIONS);
        } catch (IOException ex) {
            log.warn("Failed to count total lines of code in workspace [{}]", rootPath, ex);
            return 0L;
        }
    }

    private RepositoryMetadata readMavenMetadata(File pomFile, long totalFiles, long totalLinesOfCode) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(pomFile);
            document.getDocumentElement().normalize();

            String artifactId = readTagValue(document, "artifactId");
            String description = readTagValue(document, "description");
            String version = readTagValue(document, "version");
            long dependencyCount = document.getElementsByTagName("dependency").getLength();

            return new RepositoryMetadata(
                    artifactId != null ? artifactId : pomFile.getParentFile().getName(),
                    description,
                    version,
                    totalFiles,
                    totalLinesOfCode,
                    dependencyCount
            );
        } catch (Exception ex) {
            log.warn("Failed to parse pom.xml at [{}], falling back to default metadata", pomFile, ex);
            return defaultMetadata(pomFile.getParentFile().toPath(), totalFiles, totalLinesOfCode);
        }
    }

    private String readTagValue(Document document, String tagName) {
        NodeList nodeList = document.getElementsByTagName(tagName);

        if (nodeList.getLength() == 0) {
            return null;
        }

        return nodeList.item(0).getTextContent();
    }

    private RepositoryMetadata defaultMetadata(Path rootPath, long totalFiles, long totalLinesOfCode) {
        return new RepositoryMetadata(
                rootPath.getFileName() != null ? rootPath.getFileName().toString() : "unknown-project",
                null,
                null,
                totalFiles,
                totalLinesOfCode,
                0L
        );
    }
}