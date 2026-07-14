package com.GitSenseAI.knowledgegraph.model;

/**
 * A directed edge in the Knowledge Graph.
 *
 * @param source           source node id
 * @param target           target node id
 * @param type              relationship type
 * @param resolved         true if target points to a node actually declared in the scanned
 *                          repository (confidently resolved via SymbolIndex); false if target
 *                          is a best-effort placeholder (external library type, ambiguous
 *                          call site, or unresolved reference)
 */
public record GraphEdge(
        String source,
        String target,
        EdgeType type,
        boolean resolved
) {
}