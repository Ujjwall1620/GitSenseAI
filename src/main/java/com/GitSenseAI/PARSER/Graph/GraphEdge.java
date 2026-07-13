package com.GitSenseAI.PARSER.Graph;


import com.GitSenseAI.PARSER.Model.EdgeType;

public record GraphEdge(
        String id,
        String sourceId,
        String targetId,
        EdgeType type
) {
}