package com.GitSenseAI.Retriever.PARSER.Exception;

public class FileScanException extends RuntimeException {

    public FileScanException(String message) {
        super(message);
    }

    public FileScanException(String message, Throwable cause) {
        super(message, cause);
    }
}