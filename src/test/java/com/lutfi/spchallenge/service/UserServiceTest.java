package com.lutfi.spchallenge.service;

import com.lutfi.spchallenge.entity.User;
import com.lutfi.spchallenge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setup() {
        ZonedDateTime now = ZonedDateTime.now();

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("lutfi@lutfi.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCreatedAt(now);
        testUser.setUpdatedAt(now);
        testUser.setIsActive(true);
    }

    @Test
    public void whenFindById_thenReturnUser() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // When
        Optional<User> found = userService.getUser(1L);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("lutfi@lutfi.com");
        verify(userRepository).findById(1L);
    }

    @Test
    public void whenFindByNonExistingId_thenReturnEmpty() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<User> found = userService.getUser(999L);

        // Then
        assertThat(found).isEmpty();
        verify(userRepository).findById(999L);
    }

    @Test
    public void whenFindAll_thenReturnAllUsers() {
        // Given
        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setEmail("lutfi2@lutfi.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, secondUser));

        // When
        List<User> users = userService.getUsers();

        // Then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getEmail).containsExactly("lutfi@lutfi.com", "lutfi2@lutfi.com");
        verify(userRepository).findAll();
    }
}
