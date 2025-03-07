package com.lutfi.spchallenge.service;

import com.lutfi.spchallenge.AudioHelper;
import com.lutfi.spchallenge.entity.Phrase;
import com.lutfi.spchallenge.entity.User;
import com.lutfi.spchallenge.entity.UserPhrase;
import com.lutfi.spchallenge.repository.UserPhraseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class UserPhraseService {
    private final UserPhraseRepository userPhraseRepository;
    private final AudioHelper audioHelper;

    @Value("${file.temporary.upload}")
    private String tempFilePath;

    @Value("${file.upload}")
    private String filePath;

    @Autowired
    public UserPhraseService(UserPhraseRepository userPhraseRepository, AudioHelper audioHelper) {
        this.userPhraseRepository = userPhraseRepository;
        this.audioHelper = audioHelper;
    }

    /**
     * @param user user
     * @param file multipart file
     * @return phrase with converted file from mp4 to wav
     */
    public UserPhrase save(User user, Phrase phrase, MultipartFile file) {
        try {
            // TODO: refactor dir creation to using docker
            File tempDirectory = new File(tempFilePath);
            if (!tempDirectory.exists()) {
                tempDirectory.mkdirs();
            }

            File directory = new File(filePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String temporaryFileLoc = tempFilePath + "/" + file.getOriginalFilename();
            file.transferTo(Path.of(temporaryFileLoc));

            // convert to other format
            String fileLocation = filePath + "/" + file.getName() + ".wav";
            audioHelper.convertMP4ToWAV(temporaryFileLoc, fileLocation);

            UserPhrase userPhrase = new UserPhrase(file.getName(), fileLocation);
            userPhrase.setUser(user);
            userPhrase.setPhrase(phrase);
            UserPhrase savedUserPhrase = userPhraseRepository.save(userPhrase);

            // Delete the temporary file after processing
            File tempFile = new File(temporaryFileLoc);
            if (tempFile.exists()) {
                tempFile.delete();
            }

            return savedUserPhrase;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<UserPhrase> getPhraseByUserIdAndPhraseId(Long userId, Long phraseId) {
        return userPhraseRepository.findByUserIdAndPhraseId(userId, phraseId);
    }
}
