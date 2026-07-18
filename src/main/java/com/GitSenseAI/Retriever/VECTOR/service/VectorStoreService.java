package com.GitSenseAI.Retriever.VECTOR.service;

import com.GitSenseAI.Retriever.EMBEDDING.dto.EmbeddingResponse;
import com.GitSenseAI.Retriever.VECTOR.dto.SimilarDocument;
import com.GitSenseAI.Retriever.VECTOR.dto.VectorStoreSaveResponse;
import com.GitSenseAI.Retriever.VECTOR.exception.EmptyEmbeddingResponseException;
import com.GitSenseAI.Retriever.VECTOR.mapper.EmbeddingResultToDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreService {

    private final VectorStore vectorStore;
    private final EmbeddingResultToDocumentMapper mapper;

    public VectorStoreSaveResponse save(EmbeddingResponse embeddingResponse) {
        validate(embeddingResponse);

        List<Document> documents = mapper.toDocuments(embeddingResponse.embeddings());

        log.info("Saving {} documents to vector store [{}]...", documents.size(), vectorStore.getName());
        vectorStore.add(documents);
        log.info("Saved {} documents to vector store.", documents.size());

        return new VectorStoreSaveResponse(documents.size(), vectorStore.getName());
    }

    public List<SimilarDocument> search(String query, int topK) {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(topK).build()
        );

        return results.stream().map(this::toSimilarDocument).toList();
    }

    private SimilarDocument toSimilarDocument(Document document) {
        return new SimilarDocument(document.getId(), document.getText(), document.getScore(), document.getMetadata());
    }

    private void validate(EmbeddingResponse embeddingResponse) {
        if (embeddingResponse == null || embeddingResponse.embeddings() == null || embeddingResponse.embeddings().isEmpty()) {
            throw new EmptyEmbeddingResponseException("EmbeddingResponse contains no embeddings to store");
        }
    }
}