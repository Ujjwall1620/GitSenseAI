package com.GitSenseAI.VECTOR.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class VectorStoreExceptionHandler {

    @ExceptionHandler(EmptyEmbeddingResponseException.class)
    public ResponseEntity<Map<String, String>> handleEmptyEmbeddingResponse(EmptyEmbeddingResponseException ex) {
        log.warn("Empty embedding response received: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }
}