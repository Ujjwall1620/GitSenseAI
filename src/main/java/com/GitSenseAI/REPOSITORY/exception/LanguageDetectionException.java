package com.GitSenseAI.REPOSITORY.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class LanguageDetectionException extends RuntimeException {

    public LanguageDetectionException(String message) {
        super(message);
    }

    public LanguageDetectionException(String message, Throwable cause) {
        super(message, cause);
    }
}