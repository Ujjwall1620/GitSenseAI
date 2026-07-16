package com.GitSenseAI.ORCHESTRATION.dto;

import com.GitSenseAI.GRAPH.model.GraphEdge;
import com.GitSenseAI.GRAPH.model.GraphNode;
import com.GitSenseAI.REPOSITORY.DTO.RepositoryInfo;
import com.GitSenseAI.REPOSITORY.DTO.RepositoryMetadata;
import com.GitSenseAI.REPOSITORY.Entity.enums.BuildTools;
import com.GitSenseAI.REPOSITORY.Entity.enums.ProgramingLanguages;

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