package com.eduhab.repository;

import com.eduhab.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        User user = User.builder()
                .email("test@example.com")
                .passwordHash("hash")
                .firstName("John")
                .lastName("Doe")
                .role("STUDENT")
                .build();
        userRepository.save(user);

        User found = userRepository.findByEmail("test@example.com");

        assertNotNull(found);
        assertEquals("test@example.com", found.getEmail());
    }

    @Test
    void findByEmail_ShouldReturnNull_WhenEmailDoesNotExist() {
        User found = userRepository.findByEmail("nonexistent@example.com");

        assertNull(found);
    }

    @Test
    void save_ShouldPersistUser() {
        User user = User.builder()
                .email("save@example.com")
                .passwordHash("hash123")
                .firstName("Jane")
                .lastName("Smith")
                .role("TEACHER")
                .build();

        User saved = userRepository.save(user);

        assertNotNull(saved);
        assertTrue(saved.getId() > 0);
        assertEquals("save@example.com", saved.getEmail());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        Optional<User> found = userRepository.findById(9999);

        assertTrue(found.isEmpty());
    }

    @Test
    void deleteById_ShouldRemoveUser() {
        User user = User.builder()
                .email("delete@example.com")
                .passwordHash("hash")
                .firstName("Delete")
                .lastName("Me")
                .role("STUDENT")
                .build();
        user = userRepository.save(user);

        userRepository.deleteById(user.getId());

        assertFalse(userRepository.findById(user.getId()).isPresent());
    }
}
