package com.GitSenseAI.Retriever.GRAPH.exception;

/** Thrown when Parser Module output is structurally invalid for graph building. */
public class InvalidParserDataException extends RuntimeException {

    public InvalidParserDataException(String message) {
        super(message);
    }
}
