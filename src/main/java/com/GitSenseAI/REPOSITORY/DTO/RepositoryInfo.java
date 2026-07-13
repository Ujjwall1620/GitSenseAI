package com.GitSenseAI.REPOSITORY.DTO;

public record RepositoryInfo(Long id,
                             String owner,
                             String repositoryName,
                             String defaultBranch,
                             String lastCommitHash) {
}
