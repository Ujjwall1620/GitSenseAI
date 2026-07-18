package com.GitSenseAI.Retriever.GRAPH.model;

import java.util.Map;

/**
 * A single vertex in the Knowledge Graph.
 *
 * @param id                  deterministic unique identifier
 * @param name                simple name (e.g. method/class name)
 * @param fullyQualifiedName  fully-qualified name where resolvable (package.Class#member); same as name for unresolved/external nodes
 * @param type                structural category
 * @param filePath            source file this node was declared in; null for external/unresolved nodes
 * @param lineNumber          declaration line number; 0 for external/unresolved nodes
 * @param metadata            supplementary info (return type, field type, modifiers, etc.)
 */
public record GraphNode(
        String id,
        String name,
        String fullyQualifiedName,
        NodeType type,
        String filePath,
        int lineNumber,
        Map<String, String> metadata
) {
}