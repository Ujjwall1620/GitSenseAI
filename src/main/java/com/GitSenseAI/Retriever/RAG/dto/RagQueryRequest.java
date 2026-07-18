package com.GitSenseAI.Retriever.RAG.dto;

public record RagQueryRequest(
        String question,
        Integer topK
) {
}