package com.GitSenseAI.Bughunter.ingestion.controller;


import com.GitSenseAI.Bughunter.ingestion.dto.IngestionRequest;
import com.GitSenseAI.Bughunter.ingestion.dto.IngestionResult;
import com.GitSenseAI.Bughunter.ingestion.service.IngestionOrchestrationService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/ingest")
public class IngestionController {

    private final IngestionOrchestrationService ingestionOrchestrationService;

    @PostMapping
    public ResponseEntity<IngestionResult> ingest(@Valid @RequestBody IngestionRequest request) {
        log.info("Received ingestion request for: {}", request.repositoryUrl());

        IngestionResult result = ingestionOrchestrationService.ingest(request);

        return ResponseEntity.ok(result);
    }
}