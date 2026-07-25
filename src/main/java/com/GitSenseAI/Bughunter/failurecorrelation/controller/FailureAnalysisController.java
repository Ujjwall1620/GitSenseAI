package com.GitSenseAI.Bughunter.failurecorrelation.controller;


import com.GitSenseAI.Bughunter.TEST.dto.TestExecutionResult;
import com.GitSenseAI.Bughunter.failurecorrelation.dto.FailureAnalysisRequest;
import com.GitSenseAI.Bughunter.failurecorrelation.dto.FailureAnalysisResult;
import com.GitSenseAI.Bughunter.failurecorrelation.service.TestFailureCorrelatorService;
import com.GitSenseAI.Bughunter.failurecorrelation.service.TestFailureReviewService;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.service.KnowledgeGraphService;
import com.GitSenseAI.Retriever.PARSER.DTO.ParseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/failure-analysis")
public class FailureAnalysisController {

    private final KnowledgeGraphService knowledgeGraphService;
    private final TestFailureCorrelatorService testFailureCorrelatorService;
    private final TestFailureReviewService testFailureReviewService;

    @PostMapping("/analyze")
    public ResponseEntity<List<FailureAnalysisResult>> analyze(@RequestBody FailureAnalysisRequest request) {
        log.info("Analyzing {} test failures...", request.testExecutionResult().failures().size());

        KnowledgeGraphIndex index = knowledgeGraphService.buildKnowledgeGraph(request.parseResponse());

        var correlated = testFailureCorrelatorService.correlate(request.testExecutionResult().failures(), index);
        var analyzed = testFailureReviewService.reviewFailures(correlated);

        return ResponseEntity.ok(analyzed);
    }

}