package com.eduhab.service.auth;

import com.eduhab.domain.Student;
import com.eduhab.domain.Teacher;
import com.eduhab.domain.User;
import com.eduhab.repository.StudentRepository;
import com.eduhab.repository.TeacherRepository;
import com.eduhab.repository.UserRepository;
import com.eduhab.rest.dto.auth.AuthRequest;
import com.eduhab.rest.dto.auth.AuthResponse;
import com.eduhab.config.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        AuthResponse response = buildAuthResponse(user);
        response.setToken(jwtUtil.generateToken(user.getId(), user.getRole()));
        response.setRefreshToken(refreshTokenService.createRefreshToken(user.getId()));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return response;
    }

    @Transactional(readOnly = true)
    public AuthResponse getUserById(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;
        return buildAuthResponse(user);
    }

    public AuthResponse refreshTokens(String rawRefreshToken) {
        RefreshTokenService.RotatedTokens rotated = refreshTokenService.rotateRefreshToken(rawRefreshToken);
        int userId = rotated.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        AuthResponse response = buildAuthResponse(user);
        response.setToken(jwtUtil.generateToken(user.getId(), user.getRole()));
        response.setRefreshToken(rotated.getRefreshToken());
        return response;
    }

    public void logout(String rawRefreshToken) {
        refreshTokenService.revokeRefreshToken(rawRefreshToken);
    }

    public void logoutAll(int userId) {
        refreshTokenService.revokeAllUserTokens(userId);
    }

    private AuthResponse buildAuthResponse(User user) {
        Integer studentId = null;
        Integer teacherId = null;
        Integer groupId = null;
        String groupName = null;
        String role = user.getRole();
        if (role == null) {
            role = "";
        }

        if ("STUDENT".equals(role)) {
            Student student = studentRepository.findByUserId(user.getId());
            if (student != null) {
                studentId = student.getId();
                if (student.getGroup() != null) {
                    groupId = student.getGroup().getId();
                    groupName = student.getGroup().getName();
                }
            }
        }
        if ("TEACHER".equals(role)) {
            Teacher teacher = teacherRepository.findByUserId(user.getId());
            if (teacher != null) teacherId = teacher.getId();
        }

        return AuthResponse.builder()
                .userId(user.getId())
                .studentId(studentId)
                .teacherId(teacherId)
                .role(role)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .patronymic(user.getPatronymic())
                .groupId(groupId)
                .groupName(groupName)
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
