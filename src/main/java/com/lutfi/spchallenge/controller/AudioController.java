package com.lutfi.spchallenge.controller;

import com.lutfi.spchallenge.FileHelper;
import com.lutfi.spchallenge.entity.Phrase;
import com.lutfi.spchallenge.service.PhraseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/audio/user")
public class FileController {
    private final FileHelper fileHelper;
    private final PhraseService phraseService;

    @Autowired
    public FileController(FileHelper fileHelper, PhraseService phraseService) {
        this.fileHelper = fileHelper;
        this.phraseService = phraseService;
    }

    // not using {phrase_id} since this API purpose is to upload and convert audio
    // the system for now doesn't recognize which or data related with phrase_id
    // provided in this API, hence I assume should be not required phrase_id
    // but the response itself should return phrase_id
    @PostMapping(value = "/{user_id}/phrase")
    public ResponseEntity<Phrase> uploadAndConvert(@PathVariable Long user_id) {
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{user_id}/phrase/{phrase_id}")
    public ResponseEntity<Phrase> getPhraseByUserIdAndPhraseId(@PathVariable Long user_id, @PathVariable Long phrase_id) {
        Optional<Phrase> phraseOpt = phraseService.getPhraseByUserIdAndPhraseId(user_id, phrase_id);
        return phraseOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity upload(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Please select a file to upload"));
            }

            if (!file.getContentType().equals("video/mp4")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Only MP4 files are allowed"));
            }

            // save to files
            file.transferTo(Path.of("/home/lutfihidayat/project/speakbuddychallenge/files/"+file.getName()));
            String filename = file.getName();

            fileHelper.convertMP4ToWAV("/home/lutfihidayat/project/speakbuddychallenge/files/"+file.getName(), "/home/lutfihidayat/project/speakbuddychallenge/files/test.wav");
            Map<String, String> response = new HashMap<>();
            response.put("filename", filename);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }

}
