package com.GitSenseAI.Retriever.ORCHESTRATION.service;

import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.model.ProjectGraph;
import com.GitSenseAI.Retriever.GRAPH.service.KnowledgeGraphService;
import com.GitSenseAI.Retriever.ORCHESTRATION.dto.AnalysisResponse;
import com.GitSenseAI.Retriever.PARSER.DTO.ParseRequest;
import com.GitSenseAI.Retriever.PARSER.DTO.ParseResponse;
import com.GitSenseAI.Retriever.PARSER.Service.ParserService;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.GitRepoRequest;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.RepositoryContext;
import com.GitSenseAI.Retriever.REPOSITORY.Service.RepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisOrchestrationService {

    private final RepositoryService repositoryService;
    private final ParserService parserService;
    private final KnowledgeGraphService knowledgeGraphService;

    public AnalysisResponse analyze(GitRepoRequest repositoryRequest) {
        log.info("Starting full analysis pipeline for: {}", repositoryRequest.repositoryUrl());

        RepositoryContext repositoryContext = repositoryService.processRepository(repositoryRequest);

        ParseResponse parseResponse = parserService.parseRepository(new ParseRequest(repositoryContext));

        KnowledgeGraphIndex knowledgeGraphIndex = knowledgeGraphService.buildKnowledgeGraph(parseResponse);
        ProjectGraph graph = knowledgeGraphIndex.getGraph();

        log.info("Completed full analysis pipeline for: {}", repositoryRequest.repositoryUrl());

        return new AnalysisResponse(
                repositoryContext.repositoryInfo(),
                repositoryContext.languageInfo().primaryLanguage(),
                repositoryContext.buildToolInfo().buildTool(),
                repositoryContext.repositoryMetadata(),
                parseResponse.totalFilesScanned(),
                parseResponse.totalFilesParsed(),
                graph.getNodes().size(),
                graph.getEdges().size(),
                graph.getNodes(),
                graph.getEdges()
        );
    }
}