package com.GitSenseAI.Retriever.REPOSITORY.Repository;


import com.GitSenseAI.Retriever.REPOSITORY.Entity.GitRepoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositoryEntityRepository extends JpaRepository<GitRepoEntity, Long> {

    Optional<GitRepoEntity> findByOwnerAndRepositoryName(String owner, String repositoryName);

    boolean existsByOwnerAndRepositoryName(String owner, String repositoryName);
}