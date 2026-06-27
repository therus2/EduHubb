package com.eduhab.rest.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken;
    private int userId;
    private Integer studentId;
    private Integer teacherId;
    private String role;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Integer groupId;
    private String groupName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
