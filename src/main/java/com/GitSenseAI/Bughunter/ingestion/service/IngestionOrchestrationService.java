package com.GitSenseAI.Bughunter.ingestion.service;

import com.GitSenseAI.Bughunter.ingestion.dto.IngestionRequest;
import com.GitSenseAI.Bughunter.ingestion.dto.IngestionResult;
import com.GitSenseAI.Retriever.EMBEDDING.dto.EmbeddingResponse;
import com.GitSenseAI.Retriever.EMBEDDING.service.EmbeddingGenerationService;
import com.GitSenseAI.Retriever.GRAPH.index.KnowledgeGraphIndex;
import com.GitSenseAI.Retriever.GRAPH.service.KnowledgeGraphService;
import com.GitSenseAI.Retriever.PARSER.DTO.ParseRequest;
import com.GitSenseAI.Retriever.PARSER.DTO.ParseResponse;
import com.GitSenseAI.Retriever.PARSER.Service.ParserService;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.GitRepoRequest;
import com.GitSenseAI.Retriever.REPOSITORY.DTO.RepositoryContext;
import com.GitSenseAI.Retriever.REPOSITORY.Service.RepositoryService;
import com.GitSenseAI.Retriever.VECTOR.dto.VectorStoreSaveResponse;
import com.GitSenseAI.Retriever.VECTOR.service.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * The Flow 1 counterpart to ScanOrchestrationService: repo in, embeddings
 * stored, ready for RAG querying. Deliberately stops at storage — asking
 * questions is a separate, repeatable call against RagController, not part
 * of ingestion itself.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionOrchestrationService {

    private final RepositoryService repositoryService;
    private final ParserService parserService;
    private final KnowledgeGraphService knowledgeGraphService;
    private final EmbeddingGenerationService embeddingGenerationService;
    private final VectorStoreService vectorStoreService;

    public IngestionResult ingest(IngestionRequest ingestionRequest) {
        log.info("Starting ingestion for: {}", ingestionRequest.repositoryUrl());

        RepositoryContext repositoryContext = repositoryService.processRepository(
                new GitRepoRequest(ingestionRequest.repositoryUrl())
        );

        ParseResponse parseResponse = parserService.parseRepository(new ParseRequest(repositoryContext));
        KnowledgeGraphIndex index = knowledgeGraphService.buildKnowledgeGraph(parseResponse);

        EmbeddingResponse embeddingResponse = embeddingGenerationService.generate(index.getGraph());

        VectorStoreSaveResponse saveResponse = vectorStoreService.save(embeddingResponse);

        log.info("Ingestion completed for: {}. {} nodes embedded and stored.",
                ingestionRequest.repositoryUrl(), saveResponse.totalDocumentsSaved());

        return new IngestionResult(
                repositoryContext.repositoryInfo().repositoryName(),
                index.getGraph().getNodes().size(),
                embeddingResponse.totalNodesEmbedded(),
                saveResponse
        );
    }
}