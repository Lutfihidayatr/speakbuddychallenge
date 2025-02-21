package com.lutfi.spchallenge.controller;

import com.lutfi.spchallenge.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileController {
    private final FileHelper fileHelper;

    @Autowired
    public FileController(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
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
