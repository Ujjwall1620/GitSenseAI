package com.GitSenseAI.Retriever.GRAPH.dto;



import com.GitSenseAI.Retriever.GRAPH.model.GraphEdge;
import com.GitSenseAI.Retriever.GRAPH.model.GraphNode;

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