package com.GitSenseAI.EMBEDDING.controller;

import com.GitSenseAI.EMBEDDING.dto.EmbeddingResponse;
import com.GitSenseAI.EMBEDDING.dto.ProjectGraphRequest;
import com.GitSenseAI.EMBEDDING.mapper.ProjectGraphRequestMapper;
import com.GitSenseAI.EMBEDDING.service.EmbeddingGenerationService;
import com.GitSenseAI.GRAPH.model.ProjectGraph;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/embedding")
public class EmbeddingController {

    private final ProjectGraphRequestMapper projectGraphRequestMapper;
    private final EmbeddingGenerationService embeddingGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<EmbeddingResponse> generate(@RequestBody ProjectGraphRequest request) {
        log.info("Received embedding generation request with {} nodes.",
                request != null && request.nodes() != null ? request.nodes().size() : 0);

        ProjectGraph projectGraph = projectGraphRequestMapper.toProjectGraph(request);
        EmbeddingResponse response = embeddingGenerationService.generate(projectGraph);

        return ResponseEntity.ok(response);
    }
}