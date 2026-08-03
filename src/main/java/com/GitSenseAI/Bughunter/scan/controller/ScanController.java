package com.GitSenseAI.Bughunter.scan.controller;

import com.GitSenseAI.Bughunter.scan.dto.ScanRequest;
import com.GitSenseAI.Bughunter.scan.dto.ScanResult;
import com.GitSenseAI.Bughunter.scan.service.ScanOrchestrationService;
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
@RequestMapping("/api/v1/scan")
public class ScanController {

    private final ScanOrchestrationService scanOrchestrationService;

    @PostMapping
    public ResponseEntity<ScanResult> scan(@Valid @RequestBody ScanRequest request) {
        log.info("Received scan request for: {}", request.repositoryUrl());

        ScanResult result = scanOrchestrationService.scan(request);

        return ResponseEntity.ok(result);
    }
}