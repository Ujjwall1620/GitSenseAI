package com.GitSenseAI.Retriever.VECTOR.Controller;

import com.GitSenseAI.Retriever.EMBEDDING.dto.EmbeddingResponse;
import com.GitSenseAI.Retriever.VECTOR.dto.SimilarDocument;
import com.GitSenseAI.Retriever.VECTOR.dto.SimilaritySearchRequest;
import com.GitSenseAI.Retriever.VECTOR.dto.VectorStoreSaveResponse;
import com.GitSenseAI.Retriever.VECTOR.service.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vector-store")
public class VectorStoreController {

    private final VectorStoreService vectorStoreService;

    @PostMapping("/save")
    public ResponseEntity<VectorStoreSaveResponse> save(@RequestBody EmbeddingResponse embeddingResponse) {
        log.info("Received request to save {} embeddings to vector store.",
                embeddingResponse != null && embeddingResponse.embeddings() != null ? embeddingResponse.embeddings().size() : 0);

        return ResponseEntity.ok(vectorStoreService.save(embeddingResponse));
    }

    /**
     * Not part of RAG — this exists purely so you can verify storage actually
     * worked (per our verify-before-building-more approach), by running a
     * quick similarity search against whatever was just saved.
     */
    @PostMapping("/search")
    public ResponseEntity<List<SimilarDocument>> search(@RequestBody SimilaritySearchRequest request) {
        int topK = request.topK() != null ? request.topK() : 5;
        return ResponseEntity.ok(vectorStoreService.search(request.query(), topK));
    }
}