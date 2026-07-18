package com.GitSenseAI.Retriever.RAG.dto;

public record SourceReference(
        String nodeId,
        String nodeType,
        Double relevanceScore
) {
}