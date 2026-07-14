package com.GitSenseAI.knowledgegraph.dto;



import com.GitSenseAI.knowledgegraph.model.GraphEdge;
import com.GitSenseAI.knowledgegraph.model.GraphNode;

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