package com.GitSenseAI.EMBEDDING.service;

import com.GitSenseAI.EMBEDDING.client.EmbeddingClient;
import com.GitSenseAI.EMBEDDING.dto.EmbeddingResponse;
import com.GitSenseAI.EMBEDDING.dto.EmbeddingResult;
import com.GitSenseAI.EMBEDDING.exception.EmptyKnowledgeGraphException;
import com.GitSenseAI.EMBEDDING.exception.NullKnowledgeGraphException;
import com.GitSenseAI.EMBEDDING.mapper.NodeToTextMapper;
import com.GitSenseAI.EMBEDDING.model.NodeDocument;
import com.GitSenseAI.EMBEDDING.util.GraphIndex;
import com.GitSenseAI.GRAPH.model.ProjectGraph;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Converts a ProjectGraph (Knowledge Graph) into embedding vectors.
 * Performs no parsing, no graph construction, and no storage — purely:
 * graph node -> text document -> embedding vector.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingGenerationService {

    private final NodeToTextMapper nodeToTextMapper;
    private final EmbeddingClient embeddingClient;

    public EmbeddingResponse generate(ProjectGraph projectGraph) {
        validate(projectGraph);

        GraphIndex index = new GraphIndex(projectGraph);

        List<NodeDocument> documents = projectGraph.getNodes().stream()
                .map(node -> nodeToTextMapper.toDocument(node, index))
                .toList();

        log.info("Generating embeddings for {} knowledge graph nodes...", documents.size());

        List<EmbeddingResult> results = embeddingClient.embedAll(documents);

        int dimensions = results.isEmpty() ? 0 : results.get(0).vector().size();

        log.info("Embedding generation completed. {} vectors produced, {} dimensions each.", results.size(), dimensions);

        return new EmbeddingResponse(results.size(), dimensions, results);
    }

    private void validate(ProjectGraph projectGraph) {
        if (projectGraph == null) {
            throw new NullKnowledgeGraphException("ProjectGraph must not be null");
        }

        if (projectGraph.getNodes() == null || projectGraph.getNodes().isEmpty()) {
            throw new EmptyKnowledgeGraphException("ProjectGraph contains no nodes to embed");
        }
    }
}