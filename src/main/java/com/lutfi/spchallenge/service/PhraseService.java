package com.lutfi.spchallenge.service;

import com.lutfi.spchallenge.entity.Phrase;
import com.lutfi.spchallenge.repository.PhraseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PhraseService {
    private final PhraseRepository phraseRepository;

    @Autowired
    public PhraseService(PhraseRepository phraseRepository) {
        this.phraseRepository = phraseRepository;
    }

    public Optional<Phrase> getPhrase(Long id) {
        return phraseRepository.findById(id);
    }
}
