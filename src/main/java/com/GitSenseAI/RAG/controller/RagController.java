package com.GitSenseAI.RAG.controller;

import com.GitSenseAI.RAG.dto.RagQueryRequest;
import com.GitSenseAI.RAG.dto.RagQueryResponse;
import com.GitSenseAI.RAG.service.RagQueryService;
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
@RequestMapping("/api/v1/rag")
public class RagController {

    private final RagQueryService ragQueryService;

    @PostMapping("/query")
    public ResponseEntity<RagQueryResponse> query(@RequestBody RagQueryRequest request) {
        log.info("Received RAG query.");

        RagQueryResponse response = ragQueryService.query(request.question(), request.topK());

        return ResponseEntity.ok(response);
    }
}