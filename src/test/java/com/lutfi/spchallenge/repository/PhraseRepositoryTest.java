package com.lutfi.spchallenge.repository;

import com.lutfi.spchallenge.entity.Phrase;
import com.lutfi.spchallenge.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PhraseRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PhraseRepository phraseRepository;

    private User testUser;
    private Phrase testPhrase;

    @BeforeEach
    public void setup() {
// Create test user
        testUser = new User();
        testUser.setEmail("lutfi@lutfi.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCreatedAt(ZonedDateTime.now());
        testUser.setUpdatedAt(ZonedDateTime.now());
        testUser.setIsActive(true);
        entityManager.persist(testUser);

        // Create test phrase
        testPhrase = new Phrase();
        testPhrase.setUser(testUser);
        testPhrase.setContent("Test content");
        testPhrase.setFileName("test.wav");
        testPhrase.setCreatedAt(ZonedDateTime.now());
        testPhrase.setUpdatedAt(ZonedDateTime.now());
        entityManager.persist(testPhrase);

        entityManager.flush();
    }

    @Test
    public void whenFindByUserIdAndId_thenReturnPhrase() {
        // When
        Optional<Phrase> found = phraseRepository.findByUserIdAndPhraseId(testUser.getId(), testPhrase.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo(testPhrase.getContent());
    }

    @Test
    public void whenFindByUserIdAndId_thenReturnOptionalEmpty() {
        // When
        Optional<Phrase> found = phraseRepository.findByUserIdAndPhraseId(100000L, testPhrase.getId());

        // Then
        assertThat(found).isNotPresent();
    }
}
