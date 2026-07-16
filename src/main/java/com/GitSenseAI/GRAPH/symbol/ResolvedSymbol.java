package com.GitSenseAI.GRAPH.symbol;


import com.GitSenseAI.GRAPH.model.NodeType;

/**
 * A declared symbol registered in the SymbolIndex during pass 1 of graph
 * construction, before any edges are created.
 */
public record ResolvedSymbol(
        String id,
        String fullyQualifiedName,
        String simpleName,
        NodeType nodeType,
        String filePath,
        int lineNumber
) {
}