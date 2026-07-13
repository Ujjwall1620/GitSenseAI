package com.GitSenseAI.PARSER.DTO;


import com.GitSenseAI.PARSER.Graph.GraphEdge;
import com.GitSenseAI.PARSER.Graph.GraphNode;

import java.util.List;

public record ProjectGraphResponse(
        List<GraphNode> nodes,
        List<GraphEdge> edges
) {
}