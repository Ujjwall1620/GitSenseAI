package com.GitSenseAI.EMBEDDING.dto;

import com.GitSenseAI.GRAPH.model.GraphEdge;
import com.GitSenseAI.GRAPH.model.GraphNode;

import java.util.List;

/**
 * Wire-level request shape for POST /api/v1/embedding/generate.
 *
 * ProjectGraph itself exposes only add-style mutators (addNode/addEdge) and
 * has no all-args constructor, so it cannot be safely deserialized directly
 * from an incoming JSON body via Jackson — this DTO mirrors its contents in
 * a plain, deserializable shape, which the mapper layer then converts into
 * a real ProjectGraph.
 */
public record ProjectGraphRequest(
        List<GraphNode> nodes,
        List<GraphEdge> edges
) {
}
