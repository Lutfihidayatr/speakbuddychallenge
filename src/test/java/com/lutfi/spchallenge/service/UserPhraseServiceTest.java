package com.lutfi.spchallenge.service;

import com.lutfi.spchallenge.AudioHelper;
import com.lutfi.spchallenge.entity.Phrase;
import com.lutfi.spchallenge.entity.User;
import com.lutfi.spchallenge.entity.UserPhrase;
import jakarta.persistence.EntityManager;
import com.lutfi.spchallenge.repository.UserPhraseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPhraseServiceTest {

    @Mock
    private UserPhraseRepository userPhraseRepository;

    @Mock
    private AudioHelper audioHelper;

    @Mock
    private EntityManager entityManager;

    @Spy
    @InjectMocks
    private UserPhraseService userPhraseService;

    @TempDir
    Path tempDir;

    private User testUser;
    private Phrase testPhrase;
    private MultipartFile testFile;
    private Path tempUploadPath;
    private Path uploadPath;

    @BeforeEach
    void setUp() throws IOException {
        // Create test directories that match the implementation
        tempUploadPath = tempDir.resolve("temp");
        uploadPath = tempDir.resolve("uploads");
        Files.createDirectories(tempUploadPath);
        Files.createDirectories(uploadPath);

        // Set paths using ReflectionTestUtils (simulates @Value annotation)
        ReflectionTestUtils.setField(userPhraseService, "tempFilePath", tempUploadPath.toString());
        ReflectionTestUtils.setField(userPhraseService, "filePath", uploadPath.toString());

        // Create test data
        testUser = new User();
        testUser.setId(1L);

        testPhrase = new Phrase();
        testPhrase.setId(2L);
        testPhrase.setType("Noun Phrase");
        testPhrase.setExample("the tall building");
        testPhrase.setCreatedAt(ZonedDateTime.now());

        // Create test MP4 file
        testFile = new MockMultipartFile(
                "test-audio",
                "test-audio.mp4",
                "video/mp4",
                "test audio content".getBytes()
        );
    }

    @Test
    void save_ShouldConvertFileAndSaveUserPhrase() throws IOException {
        // Arrange
        String expectedFileName = "test-audio";
        String expectedFileLocation = uploadPath + "/test-audio.wav";

        when(userPhraseRepository.save(any(UserPhrase.class))).thenAnswer(invocation -> {
            UserPhrase savedPhrase = invocation.getArgument(0);
            savedPhrase.setId(1L);  // Simulate DB saving with ID assignment
            return savedPhrase;
        });

        // Act
        UserPhrase result = userPhraseService.save(testUser, testPhrase, testFile);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(testPhrase, result.getPhrase());
        assertEquals(expectedFileName, result.getFileName());
        assertTrue(result.getContent().endsWith(".wav"));

        // Verify
        verify(audioHelper).convertMP4ToWAV(
                contains(tempUploadPath.toString()),
                contains(uploadPath.toString())
        );
        verify(userPhraseRepository).save(any(UserPhrase.class));
    }

    @Test
    void save_ShouldCreateDirectoriesIfNotExist() throws IOException {
        // Arrange
        // Delete the directories to test creation
        Files.delete(tempUploadPath);
        Files.delete(uploadPath);

        // Create a mock MultipartFile that we can control
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.mp4");
        when(mockFile.getName()).thenReturn("test");

        when(userPhraseRepository.save(any(UserPhrase.class))).thenReturn(new UserPhrase());

        // Act
        userPhraseService.save(testUser, testPhrase, mockFile);

        // Assert
        assertTrue(Files.exists(tempUploadPath));
        assertTrue(Files.exists(uploadPath));

        // Verify
        verify(mockFile).transferTo(any(Path.class));
    }

    @Test
    void save_ShouldThrowRuntimeExceptionWhenIOExceptionOccurs() throws IOException {
        // Arrange
        MultipartFile badFile = spy(testFile);
        doThrow(new IOException("Test IO Exception")).when(badFile).transferTo(any(Path.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userPhraseService.save(testUser, testPhrase, badFile)
        );

        // Verify exception wraps an IOException
        assertTrue(exception.getCause() instanceof IOException);

        // Verify
        verify(userPhraseRepository, never()).save(any(UserPhrase.class));
    }

    @Test
    void save_ShouldDeleteTemporaryFileAfterProcessing() throws IOException {
        // Arrange
        String tempFileName = testFile.getOriginalFilename();
        Path tempFilePath = this.tempUploadPath.resolve(tempFileName);

        // Create a mock MultipartFile that we can control
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(tempFileName);
        when(mockFile.getName()).thenReturn("test-audio");

        when(userPhraseRepository.save(any(UserPhrase.class))).thenReturn(new UserPhrase());

        // Create a real file to confirm deletion
        Files.createFile(tempFilePath);
        assertTrue(Files.exists(tempFilePath));

        // Act
        userPhraseService.save(testUser, testPhrase, mockFile);

        // Assert
        assertFalse(Files.exists(tempFilePath));

        // Verify
        verify(mockFile).transferTo(any(Path.class));
    }

    @Test
    void getPhraseByUserIdAndPhraseId_ShouldReturnUserPhraseWhenFound() {
        // Arrange
        Long userId = 1L;
        Long phraseId = 2L;
        UserPhrase expectedUserPhrase = new UserPhrase();
        when(userPhraseRepository.findByUserIdAndPhraseId(userId, phraseId))
                .thenReturn(Optional.of(expectedUserPhrase));

        // Act
        Optional<UserPhrase> result = userPhraseService.getPhraseByUserIdAndPhraseId(userId, phraseId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedUserPhrase, result.get());
    }

    @Test
    void getPhraseByUserIdAndPhraseId_ShouldReturnEmptyWhenNotFound() {
        // Arrange
        Long userId = 1L;
        Long phraseId = 2L;
        when(userPhraseRepository.findByUserIdAndPhraseId(userId, phraseId))
                .thenReturn(Optional.empty());

        // Act
        Optional<UserPhrase> result = userPhraseService.getPhraseByUserIdAndPhraseId(userId, phraseId);

        // Assert
        assertFalse(result.isPresent());
    }
}