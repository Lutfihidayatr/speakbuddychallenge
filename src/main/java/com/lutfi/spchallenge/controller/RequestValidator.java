package com.lutfi.spchallenge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Component
public class RequestValidator {
    public void validateUserAndPhraseIds(Long userId, Long phraseId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }

        if (phraseId == null || phraseId <= 0) {
            throw new IllegalArgumentException("Phrase ID must be a positive number");
        }
    }

    public void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be a positive number");
        }
    }

    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload");
        }

        if (!file.getContentType().equals("video/mp4")) {
            throw new IllegalArgumentException("File format is not allowed");
        }
    }
}
