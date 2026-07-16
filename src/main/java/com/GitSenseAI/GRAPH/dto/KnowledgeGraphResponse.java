package com.GitSenseAI.GRAPH.dto;



import com.GitSenseAI.GRAPH.model.GraphEdge;
import com.GitSenseAI.GRAPH.model.GraphNode;

import java.util.List;

public record KnowledgeGraphResponse(
        int totalNodes,
        int totalEdges,
        int resolvedEdgeCount,
        int unresolvedEdgeCount,
        List<GraphNode> nodes,
        List<GraphEdge> edges
) {
}