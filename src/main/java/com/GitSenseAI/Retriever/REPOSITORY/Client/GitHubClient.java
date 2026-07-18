package com.GitSenseAI.Retriever.REPOSITORY.Client;

import com.GitSenseAI.Retriever.REPOSITORY.Config.RepositoryProperties;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.GitHubRepositoryResponse;
import com.GitSenseAI.Retriever.REPOSITORY.exception.GitHubApiException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;


@Slf4j
@Component
public class GitHubClient {

    private final RestClient restClient;

    public GitHubClient(RepositoryProperties repositoryProperties) {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(repositoryProperties.getGithubApiBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        if (repositoryProperties.getGithubToken() != null && !repositoryProperties.getGithubToken().isBlank()) {
            builder = builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + repositoryProperties.getGithubToken());
        }

        this.restClient = builder.build();
    }

    public GitHubRepositoryResponse getRepository(String owner, String repositoryName) {
        RepositoryPayload repositoryPayload = fetchRepositoryPayload(owner, repositoryName);

        if (repositoryPayload == null) {
            return null;
        }

        String lastCommitHash = fetchLatestCommitHash(owner, repositoryName, repositoryPayload.defaultBranch());

        return new GitHubRepositoryResponse(
                repositoryPayload.id(),
                repositoryPayload.owner().login(),
                repositoryPayload.name(),
                repositoryPayload.defaultBranch(),
                lastCommitHash
        );
    }

    private RepositoryPayload fetchRepositoryPayload(String owner, String repositoryName) {
        try {
            return restClient.get()
                    .uri("/repos/{owner}/{repo}", owner, repositoryName)
                    .retrieve()
                    .body(RepositoryPayload.class);
        } catch (HttpClientErrorException.NotFound notFound) {
            log.warn("Repository {}/{} not found on GitHub", owner, repositoryName);
            return null;
        } catch (Exception ex) {
            log.error("Failed to fetch repository {}/{} from GitHub API", owner, repositoryName, ex);
            throw new GitHubApiException("Failed to fetch repository from GitHub API: " + owner + "/" + repositoryName, ex);
        }
    }

    private String fetchLatestCommitHash(String owner, String repositoryName, String branch) {
        try {

            CommitPayload[] commits = restClient.get()
                    .uri("/repos/{owner}/{repo}/commits?sha={branch}&per_page=1",
                            owner,
                            repositoryName,
                            branch)
                    .retrieve()
                    .body(CommitPayload[].class);

            if (commits == null || commits.length == 0) {
                return null;
            }

            return commits[0].sha();

        } catch (Exception ex) {
            log.error("Failed to fetch latest commit for repository {}/{}", owner, repositoryName, ex);
            throw new GitHubApiException(
                    "Failed to fetch latest commit for repository: "
                            + owner + "/" + repositoryName,
                    ex
            );
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record RepositoryPayload(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("default_branch") String defaultBranch,
            @JsonProperty("owner") OwnerPayload owner
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OwnerPayload(
            @JsonProperty("login") String login
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record CommitPayload(
            @JsonProperty("sha") String sha
    ) {
    }
}