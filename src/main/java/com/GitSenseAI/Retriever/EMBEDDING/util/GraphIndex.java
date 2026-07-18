package com.GitSenseAI.Retriever.EMBEDDING.util;


import com.GitSenseAI.Retriever.GRAPH.model.EdgeType;
import com.GitSenseAI.Retriever.GRAPH.model.GraphEdge;
import com.GitSenseAI.Retriever.GRAPH.model.GraphNode;
import com.GitSenseAI.Retriever.GRAPH.model.ProjectGraph;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Lightweight, request-scoped adjacency index over a ProjectGraph.
 * Built once per embedding-generation request and used to assemble
 * relationship context (methods, supertypes, callers, etc.) when
 * generating a node's text document. Not persisted anywhere.
 */
public class GraphIndex {

    private final Map<String, GraphNode> nodesById;
    private final Map<String, List<GraphEdge>> outgoingByNodeId;
    private final Map<String, List<GraphEdge>> incomingByNodeId;

    public GraphIndex(ProjectGraph graph) {
        this.nodesById = graph.getNodes().stream()
                .collect(Collectors.toMap(GraphNode::id, node -> node, (a, b) -> a));

        this.outgoingByNodeId = graph.getEdges().stream()
                .collect(Collectors.groupingBy(GraphEdge::source));

        this.incomingByNodeId = graph.getEdges().stream()
                .collect(Collectors.groupingBy(GraphEdge::target));
    }

    public Optional<GraphNode> findNode(String id) {
        return Optional.ofNullable(nodesById.get(id));
    }

    public List<GraphNode> outgoing(String nodeId, EdgeType type) {
        return outgoingByNodeId.getOrDefault(nodeId, List.of()).stream()
                .filter(edge -> edge.type() == type)
                .map(edge -> nodesById.get(edge.target()))
                .filter(Objects::nonNull)
                .toList();
    }

    public List<GraphNode> incoming(String nodeId, EdgeType type) {
        return incomingByNodeId.getOrDefault(nodeId, List.of()).stream()
                .filter(edge -> edge.type() == type)
                .map(edge -> nodesById.get(edge.source()))
                .filter(Objects::nonNull)
                .toList();
    }
}