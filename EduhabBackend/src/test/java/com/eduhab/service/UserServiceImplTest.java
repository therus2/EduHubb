package com.eduhab.service;

import com.eduhab.domain.User;
import com.eduhab.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAll_ShouldReturnAllUsers() {
        User user1 = User.builder().id(1).email("a@test.com").firstName("A").lastName("B").role("STUDENT").build();
        User user2 = User.builder().id(2).email("c@test.com").firstName("C").lastName("D").role("TEACHER").build();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.getAll();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getById_ShouldReturnUser_WhenExists() {
        User user = User.builder().id(1).email("test@test.com").firstName("John").lastName("Doe").role("STUDENT").build();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getById_ShouldThrow_WhenNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getById(99));
    }

    @Test
    void insert_ShouldEncodePasswordAndSave() {
        User user = User.builder()
                .email("new@test.com").passwordHash("rawPassword")
                .firstName("New").lastName("User").role("STUDENT")
                .build();
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1);
            return u;
        });

        User result = userService.insert(user);

        assertEquals("encodedPassword", result.getPasswordHash());
        verify(passwordEncoder).encode("rawPassword");
        verify(userRepository).save(user);
    }

    @Test
    void update_ShouldEncodePasswordAndSave() {
        User user = User.builder().id(1).email("update@test.com")
                .passwordHash("newPassword").firstName("Up").lastName("Date")
                .role("TEACHER").build();
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNew");
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.update(user);

        assertEquals("encodedNew", result.getPasswordHash());
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(user);
    }

    @Test
    void deleteById_ShouldCallRepository() {
        userService.deleteById(1);

        verify(userRepository).deleteById(1);
    }
}
