package com.GitSenseAI.Retriever.REPOSITORY.Service;

import com.GitSenseAI.Retriever.REPOSITORY.Config.RepositoryProperties;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.RepositoryInfo;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.WorkspaceInfo;
import com.GitSenseAI.Retriever.REPOSITORY.exception.CloneFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloneService {

    private static final String GITHUB_BASE_URL = "https://github.com/";

    private final RepositoryProperties repositoryProperties;

    public void cloneRepository(RepositoryInfo repositoryInfo, WorkspaceInfo workspaceInfo) {
        String cloneUrl = buildCloneUrl(repositoryInfo);
        File targetDirectory = new File(workspaceInfo.workspacePath());

        log.info("Cloning repository [{}] into workspace [{}]", cloneUrl, targetDirectory);

        var cloneCommand = Git.cloneRepository()
                .setURI(cloneUrl)
                .setDirectory(targetDirectory)
                .setBranch(repositoryInfo.defaultBranch())
                .setDepth(repositoryProperties.getCloneDepth());

        if (hasToken()) {
            cloneCommand.setCredentialsProvider(
                    new UsernamePasswordCredentialsProvider(repositoryProperties.getGithubToken(), "")
            );
        }

        try (Git git = cloneCommand.call()) {
            log.info("Successfully cloned repository [{}]", cloneUrl);
        } catch (GitAPIException ex) {
            log.error("Failed to clone repository [{}]", cloneUrl, ex);
            throw new CloneFailedException("Failed to clone repository: " + cloneUrl, ex);
        }
    }

    private String buildCloneUrl(RepositoryInfo repositoryInfo) {
        return GITHUB_BASE_URL + repositoryInfo.owner() + "/" + repositoryInfo.repositoryName() + ".git";
    }

    private boolean hasToken() {
        return repositoryProperties.getGithubToken() != null && !repositoryProperties.getGithubToken().isBlank();
    }
}