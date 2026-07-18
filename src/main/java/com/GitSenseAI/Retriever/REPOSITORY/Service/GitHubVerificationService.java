package com.GitSenseAI.Retriever.REPOSITORY.Service;

import com.GitSenseAI.Retriever.REPOSITORY.Client.GitHubClient;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.GitHubRepositoryResponse;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.RepositoryInfo;
import com.GitSenseAI.Retriever.REPOSITORY.exception.GitHubApiException;
import com.GitSenseAI.Retriever.REPOSITORY.exception.RepositoryNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubVerificationService {

    private final GitHubClient gitHubClient;

    public RepositoryInfo verifyRepository(String owner, String repositoryName) {
        log.info("Verifying repository {}/{} via GitHub API", owner, repositoryName);

        GitHubRepositoryResponse gitHubRepositoryResponse = fetchRepository(owner, repositoryName);

        validateRepositoryExists(gitHubRepositoryResponse, owner, repositoryName);

        RepositoryInfo repositoryInfo = buildRepositoryInfo(gitHubRepositoryResponse);

        log.info("Successfully verified repository {}/{}", owner, repositoryName);

        return repositoryInfo;
    }

    private GitHubRepositoryResponse fetchRepository(String owner, String repositoryName) {
        try {
            return gitHubClient.getRepository(owner, repositoryName);
        } catch (GitHubApiException ex) {
            log.error("GitHub API call failed for repository {}/{}", owner, repositoryName, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while calling GitHub API for repository {}/{}", owner, repositoryName, ex);
            throw new GitHubApiException(
                    "Unexpected error occurred while verifying repository " + owner + "/" + repositoryName, ex);
        }
    }

    private void validateRepositoryExists(GitHubRepositoryResponse response, String owner, String repositoryName) {
        if (response == null) {
            log.warn("Repository {}/{} not found on GitHub", owner, repositoryName);
            throw new RepositoryNotFoundException(
                    "Repository not found: " + owner + "/" + repositoryName);
        }
    }

    private RepositoryInfo buildRepositoryInfo(GitHubRepositoryResponse response) {
        return new RepositoryInfo(
                response.id(),
                response.owner(),
                response.name(),
                response.defaultBranch(),
                response.lastCommitHash()
        );
    }
}
