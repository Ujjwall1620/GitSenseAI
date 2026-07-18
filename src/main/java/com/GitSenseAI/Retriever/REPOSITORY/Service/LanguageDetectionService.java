package com.GitSenseAI.Retriever.REPOSITORY.Service;

import com.GitSenseAI.Retriever.REPOSITORY.DTO.LanguageInfo;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.WorkspaceInfo;
import com.GitSenseAI.Retriever.REPOSITORY.Entity.enums.ProgramingLanguages;
import com.GitSenseAI.Retriever.REPOSITORY.Util.RespositoryFileScanner;
import com.GitSenseAI.Retriever.REPOSITORY.exception.LanguageDetectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LanguageDetectionService {

    private final RespositoryFileScanner fileScanner;

    private static final Map<String, ProgramingLanguages> EXTENSION_LANGUAGE_MAP = Map.ofEntries(
            Map.entry("java", ProgramingLanguages.JAVA),
            Map.entry("kt", ProgramingLanguages.KOTLIN),
            Map.entry("py", ProgramingLanguages.PYTHON),
            Map.entry("js", ProgramingLanguages.JAVASCRIPT),
            Map.entry("ts", ProgramingLanguages.TYPESCRIPT),
            Map.entry("go", ProgramingLanguages.GO),
            Map.entry("c",ProgramingLanguages.C),
            Map.entry("cpp", ProgramingLanguages.CPP),
            Map.entry("cs", ProgramingLanguages.CSHARP),
            Map.entry("rb", ProgramingLanguages.RUBY),
            Map.entry("php", ProgramingLanguages.PHP)
    );

    public LanguageInfo detectPrimaryLanguage(WorkspaceInfo workspaceInfo) {
        Path rootPath = Path.of(workspaceInfo.workspacePath());

        try {
            Map<String, Long> extensionCounts = fileScanner.countFilesByExtension(rootPath);
            ProgramingLanguages primaryLanguage = resolvePrimaryLanguage(extensionCounts);

            log.info("Detected primary language [{}] for workspace [{}]", primaryLanguage, rootPath);

            return new LanguageInfo(primaryLanguage, extensionCounts);
        } catch (Exception ex) {
            log.error("Failed to detect primary language in workspace [{}]", rootPath, ex);
            throw new LanguageDetectionException("Failed to detect primary language in workspace: " + rootPath, ex);
        }
    }

    private ProgramingLanguages resolvePrimaryLanguage(Map<String, Long> extensionCounts) {
        return extensionCounts.entrySet().stream()
                .filter(entry -> EXTENSION_LANGUAGE_MAP.containsKey(entry.getKey()))
                .max(Map.Entry.comparingByValue())
                .map(entry -> EXTENSION_LANGUAGE_MAP.get(entry.getKey()))
                .orElse(ProgramingLanguages.UNKNOWN);
    }
}