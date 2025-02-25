package com.lutfi.spchallenge.controller;

import com.lutfi.spchallenge.entity.User;
import com.lutfi.spchallenge.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser1;
    private User testUser2;
    private ZonedDateTime now;

    @BeforeEach
    public void setup() {
        now = ZonedDateTime.now();

        // Setup test user 1
        testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setEmail("lutfi@lutfi.com");
        testUser1.setFirstName("Lutfi");
        testUser1.setLastName("Hidayat");
        testUser1.setCreatedAt(now);
        testUser1.setUpdatedAt(now);
        testUser1.setIsActive(true);

        // Setup test user 2
        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setEmail("melisa@melisa.com");
        testUser2.setFirstName("Melisa");
        testUser2.setLastName("Maharani");
        testUser2.setCreatedAt(now);
        testUser2.setUpdatedAt(now);
        testUser2.setIsActive(true);
    }

    @Test
    public void whenFindUser_withExistingId_thenReturnUser() {
        // Given
        when(userService.getUser(anyLong())).thenReturn(Optional.of(testUser1));

        // When
        ResponseEntity<User> response = userController.findUser(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getEmail()).isEqualTo("lutfi@lutfi.com");

        verify(userService).getUser(1L);
    }

    @Test
    public void whenFindUser_withNonExistingId_thenReturnNotFound() {
        // Given
        when(userService.getUser(anyLong())).thenReturn(Optional.empty());

        // When
        ResponseEntity<User> response = userController.findUser(999L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(userService).getUser(999L);
    }

    @Test
    public void whenGetAllUsers_thenReturnUserList() {
        // Given
        List<User> userList = Arrays.asList(testUser1, testUser2);
        when(userService.getUsers()).thenReturn(userList);

        // When
        ResponseEntity<List<User>> response = userController.test();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getEmail()).isEqualTo("lutfi@lutfi.com");
        assertThat(response.getBody().get(1).getEmail()).isEqualTo("melisa@melisa.com");

        verify(userService).getUsers();
    }
}
