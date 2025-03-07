package com.lutfi.spchallenge.repository;

import com.lutfi.spchallenge.entity.Phrase;
import com.lutfi.spchallenge.entity.User;
import com.lutfi.spchallenge.entity.UserPhrase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserPhraseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserPhraseRepository userPhraseRepository;

    private User testUser;
    private Phrase testPhrase;
    private UserPhrase testUserPhrase;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setEmail("test@example.com");  // Adding required email field
        // Add other required user fields if any
        entityManager.persist(testUser);

        // Create test phrase
        testPhrase = new Phrase();
        testPhrase.setType("Noun Phrase");
        testPhrase.setExample("the tall building");
        testPhrase.setCreatedAt(ZonedDateTime.now());
        entityManager.persist(testPhrase);

        // Create test user phrase with proper associations
        testUserPhrase = new UserPhrase("test-file.mp4", "test-content");
        testUserPhrase.setUser(testUser);
        testUserPhrase.setPhrase(testPhrase);
        entityManager.persist(testUserPhrase);

        entityManager.flush();
    }

    @Test
    void findByUserIdAndPhraseId_ShouldReturnUserPhrase_WhenBothIdsMatch() {
        // Arrange
        Long userId = testUser.getId();
        Long phraseId = testUserPhrase.getId(); // Note: this is the UserPhrase ID, not Phrase ID

        // Act
        Optional<UserPhrase> result = userPhraseRepository.findByUserIdAndPhraseId(userId, phraseId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUserPhrase.getId(), result.get().getId());
        assertEquals(testUser.getId(), result.get().getUser().getId());
        assertEquals(testPhrase.getId(), result.get().getPhrase().getId());
        assertEquals("test-file.mp4", result.get().getFileName());
        assertEquals("test-content", result.get().getContent());
    }

    @Test
    void findByUserIdAndPhraseId_ShouldReturnEmpty_WhenUserIdDoesNotMatch() {
        // Arrange
        Long wrongUserId = testUser.getId() + 999; // A non-existent user ID
        Long phraseId = testUserPhrase.getId();

        // Act
        Optional<UserPhrase> result = userPhraseRepository.findByUserIdAndPhraseId(wrongUserId, phraseId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByUserIdAndPhraseId_ShouldReturnEmpty_WhenPhraseIdDoesNotMatch() {
        // Arrange
        Long userId = testUser.getId();
        Long wrongPhraseId = testUserPhrase.getId() + 999; // A non-existent phrase ID

        // Act
        Optional<UserPhrase> result = userPhraseRepository.findByUserIdAndPhraseId(userId, wrongPhraseId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByUserIdAndPhraseId_ShouldReturnEmpty_WhenBothIdsDoNotMatch() {
        // Arrange
        Long wrongUserId = testUser.getId() + 999;
        Long wrongPhraseId = testUserPhrase.getId() + 999;

        // Act
        Optional<UserPhrase> result = userPhraseRepository.findByUserIdAndPhraseId(wrongUserId, wrongPhraseId);

        // Assert
        assertFalse(result.isPresent());
    }
}