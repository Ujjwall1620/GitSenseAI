package com.GitSenseAI.Retriever.PARSER.DTO;


import com.GitSenseAI.Retriever.REPOSITORY.DTO.RepositoryContext;

public record ParseRequest(
        RepositoryContext repositoryContext
) {
}