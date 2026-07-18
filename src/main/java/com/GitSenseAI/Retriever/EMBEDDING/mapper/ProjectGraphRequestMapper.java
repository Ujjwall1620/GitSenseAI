package com.GitSenseAI.Retriever.EMBEDDING.mapper;


import com.GitSenseAI.Retriever.EMBEDDING.dto.ProjectGraphRequest;
import com.GitSenseAI.Retriever.GRAPH.model.ProjectGraph;
import org.springframework.stereotype.Component;

/** Converts the wire-level ProjectGraphRequest into a real ProjectGraph domain object. */
@Component
public class ProjectGraphRequestMapper {

    public ProjectGraph toProjectGraph(ProjectGraphRequest request) {
        ProjectGraph graph = new ProjectGraph();

        if (request == null) {
            return graph;
        }

        if (request.nodes() != null) {
            request.nodes().forEach(graph::addNode);
        }

        if (request.edges() != null) {
            request.edges().forEach(graph::addEdge);
        }

        return graph;
    }
}