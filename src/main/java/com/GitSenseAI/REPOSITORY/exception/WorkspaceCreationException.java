package com.GitSenseAI.REPOSITORY.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class WorkspaceCreationException extends RuntimeException {

    public WorkspaceCreationException(String message) {
        super(message);
    }

    public WorkspaceCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}