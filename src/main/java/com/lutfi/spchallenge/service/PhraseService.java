package com.lutfi.spchallenge.service;

import com.lutfi.spchallenge.entity.Phrase;
import com.lutfi.spchallenge.entity.User;
import com.lutfi.spchallenge.repository.PhraseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhraseService {
    private final PhraseRepository phraseRepository;

    @Autowired
    public PhraseService(PhraseRepository phraseRepository) {
        this.phraseRepository = phraseRepository;
    }

    public Phrase save(Phrase phrase) {
        return phraseRepository.save(phrase);
    }

    public Optional<Phrase> getPhraseByUserIdAndPhraseId(Long userId, Long phraseId) {
        return phraseRepository.findByUserIdAndPhraseId(userId, phraseId);
    }
}
