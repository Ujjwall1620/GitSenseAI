package com.GitSenseAI.REPOSITORY.Util;

import com.GitSenseAI.REPOSITORY.DTO.RepositoryInfo;
import com.GitSenseAI.REPOSITORY.Entity.GitRepoEntity;
import com.GitSenseAI.REPOSITORY.Entity.enums.RepositoryStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RepositoryMapper {

    public GitRepoEntity toEntity(String url, RepositoryInfo repositoryInfo, RepositoryStatus status) {
        Instant now = Instant.now();

        return GitRepoEntity.builder()
                .url(url)
                .owner(repositoryInfo.owner())
                .repositoryName(repositoryInfo.repositoryName())
                .defaultBranch(repositoryInfo.defaultBranch())
                .lastCommitHash(repositoryInfo.lastCommitHash())
                .status(status)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public RepositoryInfo toRepositoryInfo(GitRepoEntity repository) {
        return new RepositoryInfo(
                repository.getId(),
                repository.getOwner(),
                repository.getRepositoryName(),
                repository.getDefaultBranch(),
                repository.getLastCommitHash()
        );
    }
}