package com.GitSenseAI.REPOSITORY.DTO;


import com.GitSenseAI.REPOSITORY.Entity.enums.RepositoryStatus;

public record RepositoryContext(
        RepositoryInfo repositoryInfo,
        WorkspaceInfo workspaceInfo,
        BuildToolInfo buildToolInfo,
        LanguageInfo languageInfo,
        RepositoryMetadata repositoryMetadata,
        RepositoryStatus status
) {
}