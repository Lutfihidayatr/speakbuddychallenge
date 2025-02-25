package com.lutfi.spchallenge.controller;

import com.lutfi.spchallenge.entity.Phrase;
import com.lutfi.spchallenge.entity.User;
import com.lutfi.spchallenge.service.PhraseService;
import com.lutfi.spchallenge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/audio/user")
public class AudioController {
    private final PhraseService phraseService;
    private final RequestValidator validator;
    private final UserService userService;

    @Autowired
    public AudioController(PhraseService phraseService, RequestValidator validator, UserService userService) {
        this.phraseService = phraseService;
        this.validator = validator;
        this.userService = userService;
    }

    // not using {phrase_id} since this API purpose is to upload and convert audio
    // the system for now doesn't recognize which or data related with phrase_id
    // provided in this API, hence I assume should be not required phrase_id
    // but the response itself should return phrase_id
    @PostMapping(value = "/{userId}/phrase")
    public ResponseEntity<Phrase> uploadAndConvert(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        validator.validateId(userId);
        validator.validateFile(file);

        // make sure user id exist
        Optional<User> user = userService.getUser(userId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Phrase phrase = new Phrase();
        try {
            phrase = phraseService.save(user.get(), file);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(phrase);
    }

    @GetMapping(value = "/{userId}/phrase/{phraseId}")
    public ResponseEntity<Phrase> getPhraseByUserIdAndPhraseId(@PathVariable Long userId, @PathVariable Long phraseId) {
        validator.validateUserAndPhraseIds(userId, phraseId);
        Optional<Phrase> phraseOpt = phraseService.getPhraseByUserIdAndPhraseId(userId, phraseId);
        return phraseOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
