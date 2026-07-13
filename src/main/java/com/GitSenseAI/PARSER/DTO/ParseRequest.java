package com.GitSenseAI.PARSER.DTO;


import com.GitSenseAI.REPOSITORY.DTO.RepositoryContext;

public record ParseRequest(
        RepositoryContext repositoryContext
) {
}