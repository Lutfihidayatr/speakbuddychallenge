package com.lutfi.spchallenge.service;

import com.lutfi.spchallenge.AudioHelper;
import com.lutfi.spchallenge.entity.Phrase;
import com.lutfi.spchallenge.entity.User;
import com.lutfi.spchallenge.repository.PhraseRepository;
import org.bytedeco.javacv.FrameGrabber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PhraseServiceTest {
    @Mock
    private PhraseRepository phraseRepository;
    @Mock
    private AudioHelper audioHelper;
    @InjectMocks
    private PhraseService phraseService;

    @TempDir
    Path tempDir;

    private MultipartFile testFile;
    private String tempFilePath;
    private String filePath;
    private Phrase testPhrase;
    private User testUser;

    @BeforeEach
    public void setup() throws IOException {
        // Create test paths
        tempFilePath = tempDir.resolve("temp").toString();
        filePath = tempDir.resolve("files").toString();

        // Create directories
        Files.createDirectories(Path.of(tempFilePath));
        Files.createDirectories(Path.of(filePath));

        // Set paths in service
        ReflectionTestUtils.setField(phraseService, "tempFilePath", tempFilePath);
        ReflectionTestUtils.setField(phraseService, "filePath", filePath);

        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("lutfi@lutfi.com");
        testUser.setCreatedAt(ZonedDateTime.now());
        testUser.setUpdatedAt(ZonedDateTime.now());

        testPhrase = new Phrase();
        testPhrase.setId(1L);
        testPhrase.setUser(testUser);
        testPhrase.setContent("Test content");
        testPhrase.setFileName("test.txt");
        testPhrase.setCreatedAt(ZonedDateTime.now());
        testPhrase.setUpdatedAt(ZonedDateTime.now());
        testPhrase.setDeletedAt(null);

        // Create test file
        byte[] content = "test audio content".getBytes();
        testFile = new MockMultipartFile(
                "audio",
                "audio.mp4",
                "audio/mp4",
                content
        );
    }

    @Test
    public void whenGetPhraseByUserIdAndPhraseId_thenReturnPhrase() {
        // Given
        when(phraseRepository.findByUserIdAndPhraseId(anyLong(), anyLong()))
                .thenReturn(Optional.of(testPhrase));

        // When
        Optional<Phrase> found = phraseService.getPhraseByUserIdAndPhraseId(1L, 1L);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo(testPhrase.getContent());
        verify(phraseRepository).findByUserIdAndPhraseId(1L, 1L);
    }

    @Test
    public void whenGetPhraseByUserIdAndPhraseId_withNonExistingIds_thenReturnEmpty() {
        // Given
        when(phraseRepository.findByUserIdAndPhraseId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        // When/Then
        assertThat(phraseService.getPhraseByUserIdAndPhraseId(999L, 999L)).isEmpty();
    }

    @Test
    public void whenSaveWithValidFile_thenReturnSavedPhrase() throws Exception {
        // Given
        doNothing().when(audioHelper).convertMP4ToWAV(anyString(), anyString());

        when(phraseRepository.save(any(Phrase.class))).thenAnswer(invocation -> {
            Phrase savedPhrase = invocation.getArgument(0);
            savedPhrase.setId(1L);
            savedPhrase.setCreatedAt(ZonedDateTime.now());
            savedPhrase.setUpdatedAt(ZonedDateTime.now());
            return savedPhrase;
        });

        // When
        Phrase savedPhrase = phraseService.save(testUser, testFile);

        // Then
        assertThat(savedPhrase).isNotNull();
        assertThat(savedPhrase.getId()).isEqualTo(1L);
        assertThat(savedPhrase.getUser()).isEqualTo(testUser);
        assertThat(savedPhrase.getFileName()).isEqualTo("audio");
        assertThat(savedPhrase.getContent()).isEqualTo(filePath + "/audio.wav");

        // Verify file operations
        verify(audioHelper).convertMP4ToWAV(
                eq(tempFilePath + "/audio.mp4"),
                eq(filePath + "/audio.wav")
        );
        verify(phraseRepository).save(any(Phrase.class));

        // Verify temp file was created
        assertThat(Files.exists(Path.of(tempFilePath + "/audio.mp4"))).isTrue();
    }

    @Test
    public void whenSaveWithIOException_thenThrowRuntimeException() throws Exception {
        // Given
        MockMultipartFile badFile = spy(new MockMultipartFile(
                "bad.mp4",
                "bad.mp4",
                "audio/mp4",
                "test content".getBytes()
        ));

        doThrow(new IOException("Test IO exception"))
                .when(badFile).transferTo(any(Path.class));

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            phraseService.save(testUser, badFile);
        });

        verify(phraseRepository, never()).save(any(Phrase.class));
    }

    @Test
    public void whenConversionFails_thenThrowInternalError() throws Exception {
        // Given
        doThrow(new FrameGrabber.Exception("Conversion failed"))
                .when(audioHelper).convertMP4ToWAV(anyString(), anyString());

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            phraseService.save(testUser, testFile);
        });

        verify(phraseRepository, never()).save(any(Phrase.class));
    }
}
