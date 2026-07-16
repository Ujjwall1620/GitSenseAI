package com.GitSenseAI.VECTOR.exception;

public class EmptyEmbeddingResponseException extends RuntimeException {

    public EmptyEmbeddingResponseException(String message) {
        super(message);
    }
}