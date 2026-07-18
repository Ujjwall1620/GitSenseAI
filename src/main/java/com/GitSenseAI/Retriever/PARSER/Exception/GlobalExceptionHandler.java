package com.GitSenseAI.Retriever.PARSER.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnsupportedLanguageException.class)
    public ResponseEntity<Map<String, String>> handleUnsupportedLanguage(UnsupportedLanguageException ex) {
        log.warn("Unsupported language encountered: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ParsingException.class)
    public ResponseEntity<Map<String, String>> handleParsingException(ParsingException ex) {
        log.error("Parsing failed", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(FileScanException.class)
    public ResponseEntity<Map<String, String>> handleFileScanException(FileScanException ex) {
        log.error("File scanning failed", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

}