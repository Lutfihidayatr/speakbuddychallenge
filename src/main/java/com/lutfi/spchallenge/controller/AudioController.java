package com.lutfi.spchallenge.controller;

import com.lutfi.spchallenge.entity.Phrase;
import com.lutfi.spchallenge.entity.User;
import com.lutfi.spchallenge.entity.UserPhrase;
import com.lutfi.spchallenge.exception.PhraseNotFoundException;
import com.lutfi.spchallenge.service.PhraseService;
import com.lutfi.spchallenge.service.UserPhraseService;
import com.lutfi.spchallenge.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/audio/user")
public class AudioController {
    private final UserPhraseService userPhraseService;
    private final PhraseService phraseService;
    private final RequestValidator validator;
    private final UserService userService;

    @Autowired
    public AudioController(UserPhraseService userPhraseService, PhraseService phraseService, RequestValidator validator, UserService userService) {
        this.userPhraseService = userPhraseService;
        this.phraseService = phraseService;
        this.validator = validator;
        this.userService = userService;
    }

    @PostMapping(value = "/{userId}/phrase/{phraseId}")
    public ResponseEntity<UserPhrase> uploadAndConvert(@PathVariable Long userId, @PathVariable Long phraseId, @RequestParam("file") MultipartFile file) {
        validator.validateUserAndPhraseIds(userId, phraseId);
        validator.validateFile(file);

        // make sure user id exist
        Optional<User> user = userService.getUser(userId);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // make sure phrase exist
        Optional<Phrase> phraseOpt = phraseService.getPhrase(phraseId);
        if (phraseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // make sure no phrase exist with user id
        Optional<UserPhrase> userPhraseOpt = userPhraseService.getPhraseByUserIdAndPhraseId(userId, phraseId);
        if (userPhraseOpt.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        UserPhrase userPhrase = new UserPhrase();
        try {
            userPhrase = userPhraseService.save(user.get(), phraseOpt.get(), file);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(userPhrase);
    }

    @GetMapping(value = "/{userId}/phrase/{phraseId}")
    public ResponseEntity<UserPhrase> getPhraseByUserIdAndPhraseId(@PathVariable Long userId, @PathVariable Long phraseId) {
        validator.validateUserAndPhraseIds(userId, phraseId);
        Optional<UserPhrase> phraseOpt = userPhraseService.getPhraseByUserIdAndPhraseId(userId, phraseId);
        return phraseOpt.map(ResponseEntity::ok)
                .orElseThrow(() -> new PhraseNotFoundException(userId, phraseId));
    }
}
