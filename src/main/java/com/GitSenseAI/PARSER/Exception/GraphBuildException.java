package com.GitSenseAI.PARSER.Exception;

public class GraphBuildException extends RuntimeException {

    public GraphBuildException(String message) {
        super(message);
    }

    public GraphBuildException(String message, Throwable cause) {
        super(message, cause);
    }
}