package com.GitSenseAI.RAG.dto;

import java.util.List;

public record RagQueryResponse(
        String answer,
        List<SourceReference> sources
) {
}