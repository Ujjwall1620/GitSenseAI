package com.GitSenseAI.Retriever.REPOSITORY.DTO;


import com.GitSenseAI.Retriever.REPOSITORY.Entity.enums.RepositoryStatus;

public record RepositoryContext(
        RepositoryInfo repositoryInfo,
        WorkspaceInfo workspaceInfo,
        BuildToolInfo buildToolInfo,
        LanguageInfo languageInfo,
        RepositoryMetadata repositoryMetadata,
        RepositoryStatus status
) {
}