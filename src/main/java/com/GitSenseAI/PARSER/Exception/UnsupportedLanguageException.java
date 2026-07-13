package com.GitSenseAI.PARSER.Exception;

public class UnsupportedLanguageException extends RuntimeException {

    public UnsupportedLanguageException(String message) {
        super(message);
    }

    public UnsupportedLanguageException(String message, Throwable cause) {
        super(message, cause);
    }
}