package com.GitSenseAI.Retriever.VECTOR.dto;

public record VectorStoreSaveResponse(
        int totalDocumentsSaved,
        String vectorStoreName
) {
}