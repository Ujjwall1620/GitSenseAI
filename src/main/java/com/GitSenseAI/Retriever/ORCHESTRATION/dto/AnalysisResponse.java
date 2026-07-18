package com.GitSenseAI.Retriever.ORCHESTRATION.dto;

import com.GitSenseAI.Retriever.GRAPH.model.GraphEdge;
import com.GitSenseAI.Retriever.GRAPH.model.GraphNode;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.RepositoryInfo;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.RepositoryMetadata;
import com.GitSenseAI.Retriever.REPOSITORY.Entity.enums.BuildTools;
import com.GitSenseAI.Retriever.REPOSITORY.Entity.enums.ProgramingLanguages;

import java.util.List;

public record AnalysisResponse(
        RepositoryInfo repositoryInfo,
        ProgramingLanguages detectedLanguage,
        BuildTools buildTool,
        RepositoryMetadata repositoryMetadata,
        int totalFilesScanned,
        int totalFilesParsed,
        int totalGraphNodes,
        int totalGraphEdges,
        List<GraphNode> graphNodes,
        List<GraphEdge> graphEdges
) {
}