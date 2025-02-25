package com.lutfi.spchallenge.repository;

import com.lutfi.spchallenge.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @Test
    public void whenSaveUser_thenReturnUser() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setCreatedAt(ZonedDateTime.now());
        user.setUpdatedAt(ZonedDateTime.now());
        user.setIsActive(true);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getFirstName()).isEqualTo("Test");
        assertThat(savedUser.getLastName()).isEqualTo("User");
        assertThat(savedUser.getIsActive()).isTrue();
    }

    @Test
    public void whenFindById_thenReturnUser() {
        // Given
        User user = new User();
        user.setEmail("find@example.com");
        user.setFirstName("Find");
        user.setLastName("User");
        user.setCreatedAt(ZonedDateTime.now());
        user.setUpdatedAt(ZonedDateTime.now());

        User persistedUser = entityManager.persist(user);
        entityManager.flush();

        // When
        Optional<User> found = userRepository.findById(persistedUser.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("find@example.com");
        assertThat(found.get().getFirstName()).isEqualTo("Find");
    }

    @Test
    public void whenFindAll_thenReturnUserList() {
        // Given
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setFirstName("User");
        user1.setLastName("One");
        user1.setCreatedAt(ZonedDateTime.now());
        user1.setUpdatedAt(ZonedDateTime.now());

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setFirstName("User");
        user2.setLastName("Two");
        user2.setCreatedAt(ZonedDateTime.now());
        user2.setUpdatedAt(ZonedDateTime.now());

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        // When
        List<User> users = userRepository.findAll();

        // Then
        assertThat(users).isNotEmpty();
        assertThat(users.size()).isGreaterThanOrEqualTo(2);
        assertThat(users).extracting(User::getEmail).contains("user1@example.com", "user2@example.com");
    }

    @Test
    public void whenFindAll_thenReturnEmptyList() {
        // When
        List<User> users = userRepository.findAll();

        // Then
        assertThat(users.size()).isEqualTo(0);
    }

    @Test
    public void whenFindByNonExistingId_thenReturnEmpty() {
        // When
        Optional<User> found = userRepository.findById(999L);

        // Then
        assertThat(found).isEmpty();
    }
}
