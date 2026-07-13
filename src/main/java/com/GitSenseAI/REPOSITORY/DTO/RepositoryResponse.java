package com.GitSenseAI.REPOSITORY.DTO;


import com.GitSenseAI.REPOSITORY.Entity.enums.BuildTools;
import com.GitSenseAI.REPOSITORY.Entity.enums.ProgramingLanguages;
import com.GitSenseAI.REPOSITORY.Entity.enums.RepositoryStatus;

public record RepositoryResponse(
        Long id,
        String owner,
        String repositoryName,
        String defaultBranch,
        String lastCommitHash,
        String workspacePath,
        ProgramingLanguages primaryLanguage,
        BuildTools buildTool,
        RepositoryMetadata repositoryMetadata,
        RepositoryStatus status
) {
}