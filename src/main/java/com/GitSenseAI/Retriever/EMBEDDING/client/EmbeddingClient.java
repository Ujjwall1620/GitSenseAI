package com.GitSenseAI.Retriever.EMBEDDING.client;

import com.GitSenseAI.Retriever.EMBEDDING.cinfig.EmbeddingProperties;
import com.GitSenseAI.Retriever.EMBEDDING.dto.EmbeddingResult;
import com.GitSenseAI.Retriever.EMBEDDING.exception.EmbeddingGenerationException;
import com.GitSenseAI.Retriever.EMBEDDING.model.NodeDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Thin wrapper around Spring AI's EmbeddingModel. This is the only class in
 * the module that knows about Spring AI — everything else works with plain
 * NodeDocument/EmbeddingResult types, so swapping embedding providers never
 * touches the mapper, service, or controller layers.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingClient {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingProperties embeddingProperties;

    public List<EmbeddingResult> embedAll(List<NodeDocument> documents) {
        List<EmbeddingResult> results = new ArrayList<>();

        for (List<NodeDocument> batch : partition(documents, embeddingProperties.getMaxBatchSize())) {
            results.addAll(embedBatch(batch));
        }

        return results;
    }

    private List<EmbeddingResult> embedBatch(List<NodeDocument> batch) {
        List<String> texts = batch.stream().map(NodeDocument::text).toList();

        List<float[]> vectors;
        try {
            vectors = embeddingModel.embed(texts);
        } catch (Exception ex) {
            log.error("Embedding model call failed for a batch of {} nodes", batch.size(), ex);
            throw new EmbeddingGenerationException("Failed to generate embeddings for a batch of graph nodes", ex);
        }

        List<EmbeddingResult> results = new ArrayList<>();

        for (int i = 0; i < batch.size(); i++) {
            results.add(toEmbeddingResult(batch.get(i), vectors.get(i)));
        }

        return results;
    }

    private EmbeddingResult toEmbeddingResult(NodeDocument document, float[] vector) {
        List<Float> vectorList = new ArrayList<>(vector.length);
        for (float value : vector) {
            vectorList.add(value);
        }

        Map<String, String> metadata = new HashMap<>();
        metadata.put("nodeType", document.nodeType());

        return new EmbeddingResult(document.nodeId(), document.nodeType(), document.text(), vectorList, metadata);
    }

    private List<List<NodeDocument>> partition(List<NodeDocument> documents, int batchSize) {
        List<List<NodeDocument>> batches = new ArrayList<>();

        for (int i = 0; i < documents.size(); i += batchSize) {
            batches.add(documents.subList(i, Math.min(i + batchSize, documents.size())));
        }

        return batches;
    }
}