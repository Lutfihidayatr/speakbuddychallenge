package com.lutfi.spchallenge.controller;

import com.lutfi.spchallenge.entity.Phrase;
import com.lutfi.spchallenge.entity.User;
import com.lutfi.spchallenge.entity.UserPhrase;
import com.lutfi.spchallenge.exception.PhraseNotFoundException;
import com.lutfi.spchallenge.service.PhraseService;
import com.lutfi.spchallenge.service.UserPhraseService;
import com.lutfi.spchallenge.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.springframework.web.server.UnsupportedMediaTypeStatusException;

@ExtendWith(MockitoExtension.class)
public class AudioControllerTest {

    @Mock
    private UserPhraseService userPhraseService;

    @Mock
    private PhraseService phraseService;

    @Mock
    private RequestValidator validator;

    @Mock
    private UserService userService;

    @InjectMocks
    private AudioController audioController;

    private User testUser;
    private Phrase testPhrase;
    private UserPhrase testUserPhrase;
    private MultipartFile testFile;
    private Long userId = 1L;
    private Long phraseId = 2L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);

        testPhrase = new Phrase();
        testPhrase.setId(phraseId);

        testUserPhrase = new UserPhrase();
        testUserPhrase.setUser(testUser);
        testUserPhrase.setPhrase(testPhrase);

        testFile = new MockMultipartFile(
                "file",
                "audio.mp4",
                "video/mp4",
                "test audio content".getBytes()
        );
    }

    @Test
    void uploadAndConvert_Success() {
        // Arrange
        when(userService.getUser(userId)).thenReturn(Optional.of(testUser));
        when(phraseService.getPhrase(phraseId)).thenReturn(Optional.of(testPhrase));
        when(userPhraseService.getPhraseByUserIdAndPhraseId(userId, phraseId)).thenReturn(Optional.empty());
        when(userPhraseService.save(eq(testUser), eq(testPhrase), any(MultipartFile.class))).thenReturn(testUserPhrase);

        // Act
        ResponseEntity<UserPhrase> response = audioController.uploadAndConvert(userId, phraseId, testFile);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserPhrase, response.getBody());

        // Verify
        verify(validator).validateUserAndPhraseIds(userId, phraseId);
        verify(validator).validateFile(testFile);  // This should validate MP4 format
        verify(userService).getUser(userId);
        verify(phraseService).getPhrase(phraseId);
        verify(userPhraseService).getPhraseByUserIdAndPhraseId(userId, phraseId);
        verify(userPhraseService).save(testUser, testPhrase, testFile);  // This should handle MP4 to WAV conversion
    }

    @Test
    void uploadAndConvert_UserNotFound() {
        // Arrange
        when(userService.getUser(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<UserPhrase> response = audioController.uploadAndConvert(userId, phraseId, testFile);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Verify
        verify(validator).validateUserAndPhraseIds(userId, phraseId);
        verify(validator).validateFile(testFile);
        verify(userService).getUser(userId);
        verifyNoInteractions(userPhraseService);
    }

    @Test
    void uploadAndConvert_PhraseNotFound() {
        // Arrange
        when(userService.getUser(userId)).thenReturn(Optional.of(testUser));
        when(phraseService.getPhrase(phraseId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<UserPhrase> response = audioController.uploadAndConvert(userId, phraseId, testFile);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Verify
        verify(validator).validateUserAndPhraseIds(userId, phraseId);
        verify(validator).validateFile(testFile);
        verify(userService).getUser(userId);
        verify(phraseService).getPhrase(phraseId);
        verifyNoMoreInteractions(userPhraseService);
    }

    @Test
    void uploadAndConvert_InvalidFileType() {
        // Arrange
        MultipartFile invalidFile = new MockMultipartFile(
                "file",
                "audio.wav",
                "audio/wav",
                "test audio content".getBytes()
        );

        // Mock the validator to throw exception on invalid file type
        doThrow(new UnsupportedMediaTypeStatusException("Only MP4 files are supported"))
                .when(validator).validateFile(invalidFile);

        // Act & Assert
        assertThrows(UnsupportedMediaTypeStatusException.class, () ->
                audioController.uploadAndConvert(userId, phraseId, invalidFile));

        // Verify
        verify(validator).validateUserAndPhraseIds(userId, phraseId);
        verify(validator).validateFile(invalidFile);
        verifyNoInteractions(userService);
        verifyNoInteractions(phraseService);
        verifyNoInteractions(userPhraseService);
    }

    @Test
    void uploadAndConvert_UserPhraseAlreadyExists() {
        // Arrange
        when(userService.getUser(userId)).thenReturn(Optional.of(testUser));
        when(phraseService.getPhrase(phraseId)).thenReturn(Optional.of(testPhrase));
        when(userPhraseService.getPhraseByUserIdAndPhraseId(userId, phraseId)).thenReturn(Optional.of(testUserPhrase));

        // Act
        ResponseEntity<UserPhrase> response = audioController.uploadAndConvert(userId, phraseId, testFile);

        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());

        // Verify
        verify(validator).validateUserAndPhraseIds(userId, phraseId);
        verify(validator).validateFile(testFile);
        verify(userService).getUser(userId);
        verify(phraseService).getPhrase(phraseId);
        verify(userPhraseService).getPhraseByUserIdAndPhraseId(userId, phraseId);
        verify(userPhraseService, never()).save(any(), any(), any());
    }

    @Test
    void uploadAndConvert_ServiceException() {
        // Arrange
        when(userService.getUser(userId)).thenReturn(Optional.of(testUser));
        when(phraseService.getPhrase(phraseId)).thenReturn(Optional.of(testPhrase));
        when(userPhraseService.getPhraseByUserIdAndPhraseId(userId, phraseId)).thenReturn(Optional.empty());
        when(userPhraseService.save(eq(testUser), eq(testPhrase), any(MultipartFile.class))).thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<UserPhrase> response = audioController.uploadAndConvert(userId, phraseId, testFile);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        // Verify
        verify(validator).validateUserAndPhraseIds(userId, phraseId);
        verify(validator).validateFile(testFile);
        verify(userService).getUser(userId);
        verify(phraseService).getPhrase(phraseId);
        verify(userPhraseService).getPhraseByUserIdAndPhraseId(userId, phraseId);
        verify(userPhraseService).save(testUser, testPhrase, testFile);
    }

    @Test
    void getPhraseByUserIdAndPhraseId_Found() {
        // Arrange
        when(userPhraseService.getPhraseByUserIdAndPhraseId(userId, phraseId)).thenReturn(Optional.of(testUserPhrase));

        // Act
        ResponseEntity<UserPhrase> response = audioController.getPhraseByUserIdAndPhraseId(userId, phraseId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserPhrase, response.getBody());

        // Verify
        verify(validator).validateUserAndPhraseIds(userId, phraseId);
        verify(userPhraseService).getPhraseByUserIdAndPhraseId(userId, phraseId);
    }

    @Test
    void getPhraseByUserIdAndPhraseId_NotFound() {
        // Arrange
        when(userPhraseService.getPhraseByUserIdAndPhraseId(userId, phraseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PhraseNotFoundException.class, () ->
                audioController.getPhraseByUserIdAndPhraseId(userId, phraseId));

        // Verify
        verify(validator).validateUserAndPhraseIds(userId, phraseId);
        verify(userPhraseService).getPhraseByUserIdAndPhraseId(userId, phraseId);
    }
}