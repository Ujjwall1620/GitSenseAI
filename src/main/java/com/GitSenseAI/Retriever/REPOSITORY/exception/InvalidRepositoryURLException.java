package com.GitSenseAI.Retriever.REPOSITORY.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRepositoryURLException extends RuntimeException {

    public InvalidRepositoryURLException(String message) {
        super(message);
    }

    public InvalidRepositoryURLException(String message, Throwable cause) {
        super(message, cause);
    }
}