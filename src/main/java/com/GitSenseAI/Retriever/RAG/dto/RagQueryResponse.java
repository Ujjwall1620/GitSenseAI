package com.GitSenseAI.Retriever.RAG.dto;

import java.util.List;

public record RagQueryResponse(
        String answer,
        List<SourceReference> sources
) {
}