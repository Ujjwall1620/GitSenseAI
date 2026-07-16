package com.GitSenseAI.EMBEDDING.model;

/**
 * An AI-readable textual representation of a single Knowledge Graph node,
 * ready to be sent to an embedding model. This is the "semantic document"
 * described in the embedding strategy — one per graph node.
 */
public record NodeDocument(
        String nodeId,
        String nodeType,
        String text
) {
}