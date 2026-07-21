package com.GitSenseAI.Bughunter.BugDetection.util;

import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.model.EdgeType;
import com.GitSenseAI.Retriever.GRAPH.model.GraphNode;
import org.springframework.stereotype.Component;

import java.util.List;

/** Builds the context string (source + callers/callees) sent to the chat model for one method. */
@Component
public class MethodContextBuilder {

    public String buildContext(GraphNode methodNode, KnowledgeGraphIndex index) {
        StringBuilder sb = new StringBuilder();

        sb.append("Method: ").append(methodNode.name()).append("\n");
        sb.append("File: ").append(methodNode.filePath()).append(", line ").append(methodNode.lineNumber()).append("\n\n");

        sb.append("Source code:\n").append(methodNode.metadata().getOrDefault("sourceCode", "")).append("\n\n");

        List<String> callers = index.incomingNeighbors(methodNode.id(), EdgeType.CALLS).stream()
                .map(GraphNode::name).toList();
        if (!callers.isEmpty()) {
            sb.append("Called by: ").append(String.join(", ", callers)).append("\n");
        }

        List<String> callees = index.outgoingNeighbors(methodNode.id(), EdgeType.CALLS).stream()
                .map(GraphNode::name).toList();
        if (!callees.isEmpty()) {
            sb.append("Calls: ").append(String.join(", ", callees)).append("\n");
        }

        return sb.toString();
    }
}