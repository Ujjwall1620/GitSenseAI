package com.GitSenseAI.Retriever.RAG.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RagExceptionHandler {

    @ExceptionHandler(InvalidQueryException.class)
    public ResponseEntity<Map<String, String>> handleInvalidQuery(InvalidQueryException ex) {
        log.warn("Invalid RAG query: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }
}