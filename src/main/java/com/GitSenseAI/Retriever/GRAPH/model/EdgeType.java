package com.GitSenseAI.Retriever.GRAPH.model;

/** Type of relationship (edge) between two nodes in the Knowledge Graph. */
public enum EdgeType {
    CONTAINS,
    CALLS,
    EXTENDS,
    IMPLEMENTS,
    IMPORTS,
    RETURNS,
    HAS_PARAMETER,
    ANNOTATED_WITH,
    DEPENDS_ON
}