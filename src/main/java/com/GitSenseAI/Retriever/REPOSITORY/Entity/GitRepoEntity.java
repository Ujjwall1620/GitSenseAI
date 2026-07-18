package com.GitSenseAI.Retriever.REPOSITORY.Entity;

import com.GitSenseAI.Retriever.REPOSITORY.Entity.enums.RepositoryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "repositories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitRepoEntity {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "url", nullable = false, length = 512)
        private String url;

        @Column(name = "owner", nullable = false)
        private String owner;

        @Column(name = "repository_name", nullable = false)
        private String repositoryName;

        @Column(name = "default_branch")
        private String defaultBranch;

        @Column(name = "last_commit_hash")
        private String lastCommitHash;

        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false)
        private RepositoryStatus status;

        @Column(name = "created_at", nullable = false, updatable = false)
        private Instant createdAt;

        @Column(name = "updated_at", nullable = false)
        private Instant updatedAt;
    }

