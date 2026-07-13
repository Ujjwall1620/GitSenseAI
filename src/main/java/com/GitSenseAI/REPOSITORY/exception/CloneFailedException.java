package com.GitSenseAI.REPOSITORY.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CloneFailedException extends RuntimeException {

    public CloneFailedException(String message) {
        super(message);
    }

    public CloneFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}