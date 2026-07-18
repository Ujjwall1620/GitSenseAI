package com.GitSenseAI.Retriever.VECTOR.exception;

public class EmptyEmbeddingResponseException extends RuntimeException {

    public EmptyEmbeddingResponseException(String message) {
        super(message);
    }
}