package com.GitSenseAI.Retriever.GRAPH.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class KnowledgeGraphExceptionHandler {

    @ExceptionHandler(NullParserOutputException.class)
    public ResponseEntity<Map<String, String>> handleNullParserOutput(NullParserOutputException ex) {
        log.warn("Null parser output: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidParserDataException.class)
    public ResponseEntity<Map<String, String>> handleInvalidParserData(InvalidParserDataException ex) {
        log.error("Invalid parser data", ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", ex.getMessage()));
    }
}