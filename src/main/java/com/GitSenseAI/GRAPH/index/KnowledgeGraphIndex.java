package com.GitSenseAI.GRAPH.index;



import com.GitSenseAI.GRAPH.model.EdgeType;
import com.GitSenseAI.GRAPH.model.GraphEdge;
import com.GitSenseAI.GRAPH.model.GraphNode;
import com.GitSenseAI.GRAPH.model.ProjectGraph;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Indexed, queryable view over a ProjectGraph. ProjectGraph itself is just
 * two flat lists; this wraps it with id lookup and adjacency maps so
 * downstream consumers (Embedding chunking, future Impact Analysis, RAG
 * retrieval) don't have to linear-scan all edges for every lookup.
 */
public class KnowledgeGraphIndex {

    private final ProjectGraph graph;
    private final Map<String, GraphNode> nodesById;
    private final Map<String, List<GraphEdge>> outgoingByNodeId;
    private final Map<String, List<GraphEdge>> incomingByNodeId;

    public KnowledgeGraphIndex(ProjectGraph graph) {
        this.graph = graph;
        this.nodesById = graph.getNodes().stream()
                .collect(Collectors.toMap(GraphNode::id, node -> node, (a, b) -> a));
        this.outgoingByNodeId = graph.getEdges().stream()
                .collect(Collectors.groupingBy(GraphEdge::source));
        this.incomingByNodeId = graph.getEdges().stream()
                .collect(Collectors.groupingBy(GraphEdge::target));
    }

    public ProjectGraph getGraph() {
        return graph;
    }

    public Optional<GraphNode> findNode(String id) {
        return Optional.ofNullable(nodesById.get(id));
    }

    public List<GraphEdge> outgoingEdges(String nodeId) {
        return outgoingByNodeId.getOrDefault(nodeId, List.of());
    }

    public List<GraphEdge> incomingEdges(String nodeId) {
        return incomingByNodeId.getOrDefault(nodeId, List.of());
    }

    public List<GraphNode> outgoingNeighbors(String nodeId, EdgeType type) {
        return outgoingEdges(nodeId).stream()
                .filter(edge -> edge.type() == type)
                .map(edge -> nodesById.get(edge.target()))
                .filter(Objects::nonNull)
                .toList();
    }

    public List<GraphNode> incomingNeighbors(String nodeId, EdgeType type) {
        return incomingEdges(nodeId).stream()
                .filter(edge -> edge.type() == type)
                .map(edge -> nodesById.get(edge.source()))
                .filter(Objects::nonNull)
                .toList();
    }

    /** Breadth-first traversal up to maxDepth hops, following outgoing edges of any type. */
    public Set<GraphNode> neighborhood(String nodeId, int maxDepth) {
        Set<String> visited = new LinkedHashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        Map<String, Integer> depthById = new HashMap<>();

        queue.add(nodeId);
        depthById.put(nodeId, 0);

        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            int currentDepth = depthById.get(currentId);

            if (currentDepth >= maxDepth || !visited.add(currentId)) {
                continue;
            }

            for (GraphEdge edge : outgoingEdges(currentId)) {
                depthById.putIfAbsent(edge.target(), currentDepth + 1);
                queue.add(edge.target());
            }
        }

        visited.remove(nodeId);

        return visited.stream()
                .map(nodesById::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}