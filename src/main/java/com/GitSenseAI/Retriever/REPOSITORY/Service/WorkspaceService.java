package com.GitSenseAI.Retriever.REPOSITORY.Service;

import com.GitSenseAI.Retriever.REPOSITORY.Config.RepositoryProperties;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.WorkspaceInfo;
import com.GitSenseAI.Retriever.REPOSITORY.Util.WorkspaceGenerator;
import com.GitSenseAI.Retriever.REPOSITORY.exception.WorkspaceCreationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceGenerator workspaceGenerator;
    private final RepositoryProperties repositoryProperties;

    public WorkspaceInfo createWorkspace(String repositoryName) {
        Path workspacePath = workspaceGenerator.generate(repositoryProperties.getWorkspaceBaseDir(), repositoryName);

        createDirectory(workspacePath);

        String workspaceId = workspacePath.getFileName().toString();

        log.info("Created workspace [{}] at path [{}]", workspaceId, workspacePath);

        return new WorkspaceInfo(workspaceId, workspacePath.toString(), Instant.now());
    }

    public void cleanupWorkspace(WorkspaceInfo workspaceInfo) {
        Path workspacePath = Path.of(workspaceInfo.workspacePath());

        try {
            deleteRecursively(workspacePath);
            log.info("Cleaned up workspace at path [{}]", workspacePath);
        } catch (IOException ex) {
            log.error("Failed to clean up workspace at path [{}]", workspacePath, ex);
        }
    }

    private void createDirectory(Path workspacePath) {
        try {
            Files.createDirectories(workspacePath);
        } catch (IOException ex) {
            log.error("Failed to create workspace directory at path [{}]", workspacePath, ex);
            throw new WorkspaceCreationException("Failed to create workspace directory: " + workspacePath, ex);
        }
    }

    private void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }

        try (var walk = Files.walk(path)) {
            walk.sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException ex) {
                            log.warn("Failed to delete path [{}]", p, ex);
                        }
                    });
        }
    }
}