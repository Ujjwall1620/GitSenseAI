package com.GitSenseAI.VECTOR.dto;

public record VectorStoreSaveResponse(
        int totalDocumentsSaved,
        String vectorStoreName
) {
}