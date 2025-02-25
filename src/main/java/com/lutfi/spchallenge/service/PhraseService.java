package com.lutfi.spchallenge.service;

import com.lutfi.spchallenge.AudioHelper;
import com.lutfi.spchallenge.entity.Phrase;
import com.lutfi.spchallenge.entity.User;
import com.lutfi.spchallenge.repository.PhraseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class PhraseService {
    private final PhraseRepository phraseRepository;
    private final AudioHelper audioHelper;

    @Value("${file.temporary.upload}")
    private String tempFilePath;

    @Value("${file.upload}")
    private String filePath;

    @Autowired
    public PhraseService(PhraseRepository phraseRepository, AudioHelper audioHelper) {
        this.phraseRepository = phraseRepository;
        this.audioHelper = audioHelper;
    }

    public Phrase save(Phrase phrase) {
        return phraseRepository.save(phrase);
    }

    public Phrase save(User user, MultipartFile file) {
        try {
            // save to temporary file
            String filename = file.getOriginalFilename();
            file.transferTo(Path.of(tempFilePath + "/" + filename));

            // convert to other format
            String fileLocation = filePath + "/" + filename + ".wav";
            audioHelper.convertMP4ToWAV(tempFilePath + "/" + filename, fileLocation);

            Phrase phrase = new Phrase(filename, fileLocation);
            phrase.setUser(user);
            return phraseRepository.save(phrase);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new InternalError(e);
        }
    }

    public Optional<Phrase> getPhraseByUserIdAndPhraseId(Long userId, Long phraseId) {
        return phraseRepository.findByUserIdAndPhraseId(userId, phraseId);
    }
}
