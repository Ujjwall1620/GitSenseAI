package com.GitSenseAI.Bughunter.fixsuggestion.dto;

public record AiFixResponse(
        String fixedCode,
        String explanation
) {
}