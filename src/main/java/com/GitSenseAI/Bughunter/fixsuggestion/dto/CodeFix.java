package com.GitSenseAI.Bughunter.fixsuggestion.dto;

public record CodeFix(
        String originalCode,
        String fixedCode,
        String explanation
) {
}