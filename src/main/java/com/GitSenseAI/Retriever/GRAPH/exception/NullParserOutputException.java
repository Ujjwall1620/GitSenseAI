package com.GitSenseAI.Retriever.GRAPH.exception;

/** Thrown when the Knowledge Graph module receives null Parser Module output. */
public class NullParserOutputException extends RuntimeException {

    public NullParserOutputException(String message) {
        super(message);
    }
}