package com.GitSenseAI.RAG.dto;

public record RagQueryRequest(
        String question,
        Integer topK
) {
}