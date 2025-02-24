package com.lutfi.spchallenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PhraseNotFoundException extends RuntimeException {
    public PhraseNotFoundException(String message) {
        super(message);
    }

    public PhraseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhraseNotFoundException(Long phraseId) {
        super(String.format("Phrase not found with id: %d", phraseId));
    }

    public PhraseNotFoundException(Long userId, Long phraseId) {
        super(String.format("Phrase not found with id %d for user %d", phraseId, userId));
    }
}
