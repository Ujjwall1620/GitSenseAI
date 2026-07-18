package com.GitSenseAI.Retriever.REPOSITORY.DTO;


import com.GitSenseAI.Retriever.REPOSITORY.Entity.enums.ProgramingLanguages;

import java.util.Map;

public record LanguageInfo(
        ProgramingLanguages primaryLanguage,
        Map<String, Long> languageFileCounts
) {
}