package com.GitSenseAI.Bughunter.BugDetection.controller;

import com.GitSenseAI.Bughunter.BugDetection.dto.BugDetectionReport;
import com.GitSenseAI.Bughunter.BugDetection.service.BugDetectionService;
import com.GitSenseAI.Bughunter.staticanalysis.dto.StaticAnalysisReport;
import com.GitSenseAI.Bughunter.staticanalysis.service.StaticAnalysisService;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.service.KnowledgeGraphService;
import com.GitSenseAI.Retriever.PARSER.DTO.ParseResponse;
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
@RequestMapping("/api/v1/bug-detection")
public class BugDetectionController {

    private final KnowledgeGraphService knowledgeGraphService;
    private final StaticAnalysisService staticAnalysisService;
    private final BugDetectionService bugDetectionService;

    @PostMapping("/analyze")
    public ResponseEntity<BugDetectionReport> analyze(@RequestBody ParseResponse parseResponse) {
        log.info("Received bug detection request.");

        KnowledgeGraphIndex index = knowledgeGraphService.buildKnowledgeGraph(parseResponse);
        StaticAnalysisReport staticAnalysisReport = staticAnalysisService.analyze(index);
        BugDetectionReport report = bugDetectionService.analyze(index, staticAnalysisReport);

        return ResponseEntity.ok(report);
    }
}