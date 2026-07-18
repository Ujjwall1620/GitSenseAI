package com.GitSenseAI.Retriever.REPOSITORY.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubRepositoryResponse(
        Long id,
        String owner,
        String name,
        String defaultBranch,
        String lastCommitHash
) {
}