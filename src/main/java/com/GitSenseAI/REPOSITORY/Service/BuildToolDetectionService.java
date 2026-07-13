package com.GitSenseAI.REPOSITORY.Service;

import com.GitSenseAI.REPOSITORY.DTO.BuildToolInfo;
import com.GitSenseAI.REPOSITORY.DTO.WorkspaceInfo;
import com.GitSenseAI.REPOSITORY.Entity.enums.BuildTools;
import com.GitSenseAI.REPOSITORY.Util.RespositoryFileScanner;
import com.GitSenseAI.REPOSITORY.exception.BuildToolDetectionException;
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

            log.warn("No recognized build tool found in workspace [{}]", rootPath);
            return new BuildToolInfo(BuildTools.UNKNOWN, null);
        } catch (Exception ex) {
            log.error("Failed to detect build tool in workspace [{}]", rootPath, ex);
            throw new BuildToolDetectionException("Failed to detect build tool in workspace: " + rootPath, ex);
        }
    }
}