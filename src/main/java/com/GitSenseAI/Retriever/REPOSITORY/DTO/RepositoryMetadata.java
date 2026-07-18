package com.GitSenseAI.Retriever.REPOSITORY.DTO;

public record RepositoryMetadata(
        String projectName,
        String description,
        String version,
        long totalFiles,
        long totalLinesOfCode,
        long dependencyCount
) {
}