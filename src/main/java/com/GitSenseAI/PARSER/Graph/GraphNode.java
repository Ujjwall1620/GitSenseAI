package com.GitSenseAI.PARSER.Graph;


import com.GitSenseAI.PARSER.Model.NodeType;

public record GraphNode(
        String id,
        String filePath,
        int lineNumber,
        String name,
        NodeType type
) {
}