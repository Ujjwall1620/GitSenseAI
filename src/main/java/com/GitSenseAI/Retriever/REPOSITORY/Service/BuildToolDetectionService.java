package com.GitSenseAI.Retriever.REPOSITORY.Service;

import com.GitSenseAI.Retriever.REPOSITORY.DTO.BuildToolInfo;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.WorkspaceInfo;
import com.GitSenseAI.Retriever.REPOSITORY.Entity.enums.BuildTools;
import com.GitSenseAI.Retriever.REPOSITORY.Util.RespositoryFileScanner;
import com.GitSenseAI.Retriever.REPOSITORY.exception.BuildToolDetectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuildToolDetectionService {

    private final RespositoryFileScanner fileScanner;

    public BuildToolInfo detectBuildTool(WorkspaceInfo workspaceInfo) {
        Path rootPath = Path.of(workspaceInfo.workspacePath());

        try {
            if (fileScanner.fileExists(rootPath, "pom.xml")) {
                return new BuildToolInfo(BuildTools.MAVEN, rootPath.resolve("pom.xml").toString());
            }

            if (fileScanner.fileExists(rootPath, "build.gradle") || fileScanner.fileExists(rootPath, "build.gradle.kts")) {
                String buildFile = fileScanner.fileExists(rootPath, "build.gradle") ? "build.gradle" : "build.gradle.kts";
                return new BuildToolInfo(BuildTools.GRADLE, rootPath.resolve(buildFile).toString());
            }

            if (fileScanner.fileExists(rootPath, "package.json")) {
                BuildTools buildTool = fileScanner.fileExists(rootPath, "yarn.lock") ? BuildTools.YARN : BuildTools.NPM;
                return new BuildToolInfo(buildTool, rootPath.resolve("package.json").toString());
            }

            if (fileScanner.fileExists(rootPath, "requirements.txt") || fileScanner.fileExists(rootPath, "setup.py")) {
                String buildFile = fileScanner.fileExists(rootPath, "requirements.txt") ? "requirements.txt" : "setup.py";
                return new BuildToolInfo(BuildTools.PIP, rootPath.resolve(buildFile).toString());
            }

            if (fileScanner.fileExists(rootPath, "go.mod")) {
                return new BuildToolInfo(BuildTools.GO, rootPath.resolve("go.mod").toString());
            }

            if (fileScanner.anyFileWithExtension(rootPath, "csproj") || fileScanner.anyFileWithExtension(rootPath, "sln")) {
                return new BuildToolInfo(BuildTools.DOTNET, null);
            }

            if (fileScanner.fileExists(rootPath, "Gemfile")) {
                return new BuildToolInfo(BuildTools.BUNDLER, rootPath.resolve("Gemfile").toString());
            }

            if (fileScanner.fileExists(rootPath, "composer.json")) {
                return new BuildToolInfo(BuildTools.PHPUNIT, rootPath.resolve("composer.json").toString());
            }

            if (fileScanner.fileExists(rootPath, "CMakeLists.txt")) {
                return new BuildToolInfo(BuildTools.CMAKE, rootPath.resolve("CMakeLists.txt").toString());
            }

            if (fileScanner.fileExists(rootPath, "Makefile")) {
                return new BuildToolInfo(BuildTools.MAKE, rootPath.resolve("Makefile").toString());
            }

            log.warn("No recognized build tool found in workspace [{}]", rootPath);
            return new BuildToolInfo(BuildTools.UNKNOWN, null);
        } catch (Exception ex) {
            log.error("Failed to detect build tool in workspace [{}]", rootPath, ex);
            throw new BuildToolDetectionException("Failed to detect build tool in workspace: " + rootPath, ex);
        }
    }
}