package com.GitSenseAI.EMBEDDING.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class EmbeddingExceptionHandler {

    @ExceptionHandler(NullKnowledgeGraphException.class)
    public ResponseEntity<Map<String, String>> handleNullGraph(NullKnowledgeGraphException ex) {
        log.warn("Null knowledge graph received: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EmptyKnowledgeGraphException.class)
    public ResponseEntity<Map<String, String>> handleEmptyGraph(EmptyKnowledgeGraphException ex) {
        log.warn("Empty knowledge graph received: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EmbeddingGenerationException.class)
    public ResponseEntity<Map<String, String>> handleGenerationFailure(EmbeddingGenerationException ex) {
        log.error("Embedding generation failed", ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", ex.getMessage()));
    }
}
