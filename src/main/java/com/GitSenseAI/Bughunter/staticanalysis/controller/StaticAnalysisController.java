package com.GitSenseAI.Bughunter.staticanalysis.controller;

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
@RequestMapping("/api/v1/static-analysis")
public class StaticAnalysisController {

    private final KnowledgeGraphService knowledgeGraphService;
    private final StaticAnalysisService staticAnalysisService;

    @PostMapping("/analyze")
    public ResponseEntity<StaticAnalysisReport> analyze(@RequestBody ParseResponse parseResponse) {
        log.info("Received static analysis request.");

        KnowledgeGraphIndex index = knowledgeGraphService.buildKnowledgeGraph(parseResponse);
        StaticAnalysisReport report = staticAnalysisService.analyze(index);

        return ResponseEntity.ok(report);
    }
}