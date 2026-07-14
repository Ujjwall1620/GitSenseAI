package com.GitSenseAI.REPOSITORY.Controller;

import com.GitSenseAI.REPOSITORY.DTO.GitRepoRequest;
import com.GitSenseAI.REPOSITORY.DTO.RepositoryContext;
import com.GitSenseAI.REPOSITORY.DTO.RepositoryResponse;
import com.GitSenseAI.REPOSITORY.Service.RepositoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/GS")
public class RepositoryController {

    private final RepositoryService repositoryService;

    @PostMapping("/analyze")
    public ResponseEntity<RepositoryResponse> analyzeRepository(@Valid @RequestBody GitRepoRequest request) {
        log.info("Received repository analysis request for URL: {}", request.repositoryUrl());

        RepositoryContext context = repositoryService.processRepository(request);
        RepositoryResponse response = buildResponse(context);
        log.info("Completed repository analysis for URL: {}", request.repositoryUrl());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private RepositoryResponse buildResponse(RepositoryContext context) {
        return new RepositoryResponse(
                context.repositoryInfo().id(),
                context.repositoryInfo().owner(),
                context.repositoryInfo().repositoryName(),
                context.repositoryInfo().defaultBranch(),
                context.repositoryInfo().lastCommitHash(),
                context.workspaceInfo().workspacePath(),
                context.languageInfo().primaryLanguage(),
                context.buildToolInfo().buildTool(),
                context.repositoryMetadata(),
                context.status()
        );
    }
}