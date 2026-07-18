package com.GitSenseAI.Retriever.REPOSITORY.Service;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.*;
import com.GitSenseAI.Retriever.REPOSITORY.Entity.GitRepoEntity;
import com.GitSenseAI.Retriever.REPOSITORY.Entity.enums.RepositoryStatus;
import com.GitSenseAI.Retriever.REPOSITORY.Repository.RepositoryEntityRepository;
import com.GitSenseAI.Retriever.REPOSITORY.Util.GitHubUrlParser;
import com.GitSenseAI.Retriever.REPOSITORY.Util.RepositoryMapper;
import com.GitSenseAI.Retriever.REPOSITORY.exception.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final GitHubUrlParser gitHubUrlParser;
    private final GitHubVerificationService gitHubVerificationService;
    private final WorkspaceService workspaceService;
    private final CloneService cloneService;
    private final BuildToolDetectionService buildToolDetectionService;
    private final LanguageDetectionService languageDetectionService;
    private final MetadataService metadataService;
    private final RepositoryMapper repositoryMapper;
    private final RepositoryEntityRepository repositoryEntityRepository;

    public RepositoryContext processRepository(@Valid GitRepoRequest request) {
        log.info("Starting repository processing workflow for URL: {}", request.repositoryUrl());

        String[] ownerAndName = extractOwnerAndRepositoryName(request.repositoryUrl());
        String owner = ownerAndName[0];
        String repositoryName = ownerAndName[1];

        RepositoryInfo repositoryInfo = verifyRepository(owner, repositoryName);

        GitRepoEntity repository = persistInitialRepository(request.repositoryUrl(), repositoryInfo);

        WorkspaceInfo workspaceInfo = createWorkspace(repositoryName);

        cloneRepository(repositoryInfo, workspaceInfo);

        var buildToolInfo = detectBuildTool(workspaceInfo);

        var languageInfo = detectPrimaryLanguage(workspaceInfo);

        RepositoryMetadata repositoryMetadata = readMetadata(workspaceInfo);

        updateRepositoryStatus(repository, RepositoryStatus.ANALYZED);

        log.info("Completed repository processing workflow for URL: {}", request.repositoryUrl());

        return new RepositoryContext(
                repositoryInfo,
                workspaceInfo,
                buildToolInfo,
                languageInfo,
                repositoryMetadata,
                RepositoryStatus.ANALYZED
        );
    }

    private String[] extractOwnerAndRepositoryName(String repositoryUrl) {
        try {
            return gitHubUrlParser.parse(repositoryUrl);
        } catch (InvalidRepositoryURLException ex) {
            log.error("Invalid GitHub URL provided: {}", repositoryUrl, ex);
            throw ex;
        }
    }

    private RepositoryInfo verifyRepository(String owner, String repositoryName) {
        try {
            return gitHubVerificationService.verifyRepository(owner, repositoryName);
        } catch (GitHubApiException ex) {
            log.error("Failed to verify repository {}/{} via GitHub API", owner, repositoryName, ex);
            throw ex;
        }
    }

    private GitRepoEntity persistInitialRepository(String repositoryUrl, RepositoryInfo repositoryInfo) {
        GitRepoEntity repository = repositoryMapper.toEntity(repositoryUrl, repositoryInfo, RepositoryStatus.VERIFIED);
        GitRepoEntity savedRepository = repositoryEntityRepository.save(repository);
        log.info("Persisted repository record with id: {}", savedRepository.getId());
        return savedRepository;
    }

    private WorkspaceInfo createWorkspace(String repositoryName) {
        try {
            return workspaceService.createWorkspace(repositoryName);
        } catch (WorkspaceCreationException ex) {
            log.error("Failed to create workspace for repository: {}", repositoryName, ex);
            throw ex;
        }
    }

    private void cloneRepository(RepositoryInfo repositoryInfo, WorkspaceInfo workspaceInfo) {
        try {
            cloneService.cloneRepository(repositoryInfo, workspaceInfo);
        } catch (CloneFailedException ex) {
            log.error("Failed to clone repository: {}/{}", repositoryInfo.owner(), repositoryInfo.repositoryName(), ex);
            throw ex;
        }
    }

    private BuildToolInfo detectBuildTool(WorkspaceInfo workspaceInfo) {
        try {
            return buildToolDetectionService.detectBuildTool(workspaceInfo);
        } catch (BuildToolDetectionException ex) {
            log.error("Failed to detect build tool for workspace: {}", workspaceInfo.workspacePath(), ex);
            throw ex;
        }
    }

    private LanguageInfo detectPrimaryLanguage(WorkspaceInfo workspaceInfo) {
        try {
            return languageDetectionService.detectPrimaryLanguage(workspaceInfo);
        } catch (LanguageDetectionException ex) {
            log.error("Failed to detect primary language for workspace: {}", workspaceInfo.workspacePath(), ex);
            throw ex;
        }
    }

    private RepositoryMetadata readMetadata(WorkspaceInfo workspaceInfo) {
        return metadataService.readMetadata(workspaceInfo);
    }

    private void updateRepositoryStatus(GitRepoEntity repository, RepositoryStatus status) {
        repository.setStatus(status);
        repositoryEntityRepository.save(repository);
        log.info("Updated repository id: {} to status: {}", repository.getId(), status);
    }
}