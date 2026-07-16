package com.GitSenseAI.GRAPH.controller;

import com.GitSenseAI.PARSER.DTO.ParseResponse;
import com.GitSenseAI.GRAPH.dto.KnowledgeGraphResponse;
import com.GitSenseAI.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.GRAPH.model.GraphEdge;
import com.GitSenseAI.GRAPH.model.ProjectGraph;
import com.GitSenseAI.GRAPH.service.KnowledgeGraphService;
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
@RequestMapping("/api/v1/knowledge-graph")
public class KnowledgeGraphController {

    private final KnowledgeGraphService knowledgeGraphService;

    @PostMapping("/build")
    public ResponseEntity<KnowledgeGraphResponse> build(@RequestBody ParseResponse parseResponse) {
        log.info("Received knowledge graph build request.");

        KnowledgeGraphIndex index = knowledgeGraphService.buildKnowledgeGraph(parseResponse);
        ProjectGraph graph = index.getGraph();

        long resolvedCount = graph.getEdges().stream().filter(GraphEdge::resolved).count();

        KnowledgeGraphResponse response = new KnowledgeGraphResponse(
                graph.getNodes().size(),
                graph.getEdges().size(),
                (int) resolvedCount,
                graph.getEdges().size() - (int) resolvedCount,
                graph.getNodes(),
                graph.getEdges()
        );

        return ResponseEntity.ok(response);
    }
}