package com.GitSenseAI.Bughunter.ingestion.dto;

import com.GitSenseAI.Retriever.VECTOR.dto.VectorStoreSaveResponse;

public record IngestionResult(
        String repositoryName,
        int totalGraphNodes,
        int totalEmbeddingsGenerated,
        VectorStoreSaveResponse vectorStoreSaveResponse
) {
}