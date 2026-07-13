package com.GitSenseAI.REPOSITORY.Util;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.UUID;

@Component
public class WorkspaceGenerator {

    public Path generate(String baseDir, String repositoryName) {
        String sanitizedName = repositoryName.replaceAll("[^A-Za-z0-9_-]", "_");
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        String folderName = sanitizedName + "-" + uniqueSuffix;

        return Path.of(baseDir, folderName);
    }
}