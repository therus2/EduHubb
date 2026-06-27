package com.eduhab.service;

import com.eduhab.config.jwt.JwtUtil;
import com.eduhab.domain.*;
import com.eduhab.repository.*;
import com.eduhab.rest.dto.auth.AuthRequest;
import com.eduhab.rest.dto.auth.AuthResponse;
import com.eduhab.service.auth.AuthService;
import com.eduhab.service.auth.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsValid() {
        User user = User.builder()
                .id(1).email("user@test.com").passwordHash("encodedPass")
                .firstName("John").lastName("Doe").role("STUDENT")
                .isActive(true).createdAt(LocalDateTime.now())
                .build();
        AuthRequest request = new AuthRequest("user@test.com", "rawPass");

        when(userRepository.findByEmail("user@test.com")).thenReturn(user);
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);
        when(jwtUtil.generateToken(1, "STUDENT")).thenReturn("mock-token");
        when(refreshTokenService.createRefreshToken(1)).thenReturn("mock-refresh-token");
        when(userRepository.save(user)).thenReturn(user);

        Student student = new Student();
        student.setId(10);
        student.setUser(user);
        StudentGroup group = StudentGroup.builder().id(5).name("10A").build();
        student.setGroup(group);

        when(studentRepository.findByUserId(1)).thenReturn(student);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-token", response.getToken());
        assertEquals("mock-refresh-token", response.getRefreshToken());
        assertEquals(1, response.getUserId());
        assertEquals(10, response.getStudentId());
        assertEquals(5, response.getGroupId());
        assertEquals("10A", response.getGroupName());
        assertEquals("STUDENT", response.getRole());
    }

    @Test
    void login_ShouldThrow_WhenEmailNotFound() {
        AuthRequest request = new AuthRequest("nonexistent@test.com", "pass");
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(null);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrow_WhenPasswordWrong() {
        User user = User.builder()
                .email("user@test.com").passwordHash("encodedPass")
                .firstName("John").lastName("Doe").role("STUDENT")
                .build();
        AuthRequest request = new AuthRequest("user@test.com", "wrongPass");

        when(userRepository.findByEmail("user@test.com")).thenReturn(user);
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldIncludeTeacherId_WhenRoleIsTeacher() {
        User user = User.builder()
                .id(2).email("teacher@test.com").passwordHash("hash")
                .firstName("Jane").lastName("Smith").role("TEACHER")
                .isActive(true).build();
        AuthRequest request = new AuthRequest("teacher@test.com", "pass");

        when(userRepository.findByEmail("teacher@test.com")).thenReturn(user);
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);
        when(jwtUtil.generateToken(2, "TEACHER")).thenReturn("mock-token");
        when(refreshTokenService.createRefreshToken(2)).thenReturn("mock-refresh-token");
        when(userRepository.save(user)).thenReturn(user);

        Teacher teacher = Teacher.builder().id(20).user(user).build();
        when(teacherRepository.findByUserId(2)).thenReturn(teacher);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(20, response.getTeacherId());
        assertNull(response.getStudentId());
    }

    @Test
    void getUserById_ShouldReturnAuthResponse_WhenUserExists() {
        User user = User.builder()
                .id(1).email("user@test.com").passwordHash("hash")
                .firstName("John").lastName("Doe").role("STUDENT")
                .isActive(true).build();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Student student = new Student();
        student.setId(10);
        student.setUser(user);
        when(studentRepository.findByUserId(1)).thenReturn(student);

        AuthResponse response = authService.getUserById(1);

        assertNotNull(response);
        assertEquals(1, response.getUserId());
    }

    @Test
    void getUserById_ShouldReturnNull_WhenUserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        AuthResponse response = authService.getUserById(99);

        assertNull(response);
    }
}
