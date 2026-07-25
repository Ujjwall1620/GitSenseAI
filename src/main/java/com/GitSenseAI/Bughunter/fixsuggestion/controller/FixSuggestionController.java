package com.GitSenseAI.Bughunter.fixsuggestion.controller;

import com.GitSenseAI.Bughunter.fixsuggestion.dto.FixSuggestionReport;
import com.GitSenseAI.Bughunter.fixsuggestion.dto.FixSuggestionRequest;
import com.GitSenseAI.Bughunter.fixsuggestion.service.FixSuggestionService;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.service.KnowledgeGraphService;
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
@RequestMapping("/api/v1/fix-suggestion")
public class FixSuggestionController {

    private final KnowledgeGraphService knowledgeGraphService;
    private final FixSuggestionService fixSuggestionService;

    @PostMapping("/generate")
    public ResponseEntity<FixSuggestionReport> generate(@RequestBody FixSuggestionRequest request) {
        KnowledgeGraphIndex index = knowledgeGraphService.buildKnowledgeGraph(request.parseResponse());
        FixSuggestionReport report = fixSuggestionService.generateFixes(request.bugReport(), index);

        return ResponseEntity.ok(report);
    }
}