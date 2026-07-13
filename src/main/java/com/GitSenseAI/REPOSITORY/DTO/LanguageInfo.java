package com.GitSenseAI.REPOSITORY.DTO;


import com.GitSenseAI.REPOSITORY.Entity.enums.ProgramingLanguages;

import java.util.Map;

public record LanguageInfo(
        ProgramingLanguages primaryLanguage,
        Map<String, Long> languageFileCounts
) {
}