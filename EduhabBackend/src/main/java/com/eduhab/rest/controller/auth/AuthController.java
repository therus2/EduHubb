package com.eduhab.rest.controller.auth;

import com.eduhab.rest.dto.auth.AuthRequest;
import com.eduhab.rest.dto.auth.AuthResponse;
import com.eduhab.rest.dto.auth.RefreshTokenRequest;
import com.eduhab.rest.dto.auth.RefreshTokenResponse;
import com.eduhab.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/auth/refresh")
    public RefreshTokenResponse refresh(@RequestBody RefreshTokenRequest request) {
        AuthResponse auth = authService.refreshTokens(request.getRefreshToken());
        return RefreshTokenResponse.builder()
                .token(auth.getToken())
                .refreshToken(auth.getRefreshToken())
                .build();
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (request != null && request.getRefreshToken() != null) {
            authService.logout(request.getRefreshToken());
        } else if (auth != null && auth.getPrincipal() instanceof Integer) {
            authService.logoutAll((int) auth.getPrincipal());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/auth/me")
    public AuthResponse getMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Integer)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        int userId = (int) auth.getPrincipal();
        AuthResponse response = authService.getUserById(userId);
        if (response == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return response;
    }
}
