package com.lutfi.spchallenge.controller;

import com.lutfi.spchallenge.entity.Phrase;
import com.lutfi.spchallenge.entity.User;
import com.lutfi.spchallenge.exception.PhraseNotFoundException;
import com.lutfi.spchallenge.service.PhraseService;
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

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AudioControllerTest {
    @Mock
    private PhraseService phraseService;

    @Mock
    private UserService userService;

    @Mock
    private RequestValidator validator;

    @InjectMocks
    private AudioController audioController;

    private User testUser;
    private Phrase testPhrase;
    private MultipartFile testFile;
    private ZonedDateTime now;

    @BeforeEach
    public void setup() {
        now = ZonedDateTime.now();

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("lutfi@lutfi.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCreatedAt(now);
        testUser.setUpdatedAt(now);
        testUser.setIsActive(true);

        // Setup test phrase
        testPhrase = new Phrase();
        testPhrase.setId(1L);
        testPhrase.setUser(testUser);
        testPhrase.setFileName("test-audio.mp4");
        testPhrase.setContent("/path/to/test-audio.wav");
        testPhrase.setCreatedAt(now);
        testPhrase.setUpdatedAt(now);

        // Setup test file
        testFile = new MockMultipartFile(
                "file",
                "test-audio.mp4",
                "audio/mp4",
                "test audio content".getBytes()
        );
    }

    @Test
    public void whenUploadAndConvert_withValidUserAndFile_thenReturnPhrase() {
        // Given
        when(userService.getUser(anyLong())).thenReturn(Optional.of(testUser));
        when(phraseService.save(any(User.class), any(MultipartFile.class))).thenReturn(testPhrase);
        doNothing().when(validator).validateId(anyLong());
        doNothing().when(validator).validateFile(any(MultipartFile.class));

        // When
        ResponseEntity<Phrase> response = audioController.uploadAndConvert(1L, testFile);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getUser()).isEqualTo(testUser);

        verify(userService).getUser(1L);
        verify(phraseService).save(testUser, testFile);
        verify(validator).validateId(1L);
        verify(validator).validateFile(testFile);
    }

    @Test
    public void whenUploadAndConvert_withNonExistingUser_thenReturnNotFound() {
        // Given
        when(userService.getUser(anyLong())).thenReturn(Optional.empty());
        doNothing().when(validator).validateId(anyLong());
        doNothing().when(validator).validateFile(any(MultipartFile.class));

        // When
        ResponseEntity<Phrase> response = audioController.uploadAndConvert(999L, testFile);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(userService).getUser(999L);
        verify(phraseService, never()).save(any(User.class), any(MultipartFile.class));
    }

    @Test
    public void whenUploadAndConvert_withServiceException_thenReturnInternalError() {
        // Given
        when(userService.getUser(anyLong())).thenReturn(Optional.of(testUser));
        when(phraseService.save(any(User.class), any(MultipartFile.class)))
                .thenThrow(new RuntimeException("Conversion failed"));
        doNothing().when(validator).validateId(anyLong());
        doNothing().when(validator).validateFile(any(MultipartFile.class));

        // When
        ResponseEntity<Phrase> response = audioController.uploadAndConvert(1L, testFile);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();

        verify(userService).getUser(1L);
        verify(phraseService).save(testUser, testFile);
    }

    @Test
    public void whenGetPhraseByUserIdAndPhraseId_withExistingIds_thenReturnPhrase() {
        // Given
        when(phraseService.getPhraseByUserIdAndPhraseId(anyLong(), anyLong()))
                .thenReturn(Optional.of(testPhrase));
        doNothing().when(validator).validateUserAndPhraseIds(anyLong(), anyLong());

        // When
        ResponseEntity<Phrase> response = audioController.getPhraseByUserIdAndPhraseId(1L, 1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);

        verify(phraseService).getPhraseByUserIdAndPhraseId(1L, 1L);
        verify(validator).validateUserAndPhraseIds(1L, 1L);
    }

    @Test
    public void whenGetPhraseByUserIdAndPhraseId_withNonExistingIds_thenReturnNotFound() {
        // Given
        when(phraseService.getPhraseByUserIdAndPhraseId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        doNothing().when(validator).validateUserAndPhraseIds(anyLong(), anyLong());

        // When
        // Act & Assert
        PhraseNotFoundException exception = assertThrows(
                PhraseNotFoundException.class,
                () -> audioController.getPhraseByUserIdAndPhraseId(999L, 999L)
        );

        // Then
        assertEquals("Phrase not found with id 999 for user 999", exception.getMessage());
        verify(phraseService, times(1)).getPhraseByUserIdAndPhraseId(999L, 999L);
    }
}
