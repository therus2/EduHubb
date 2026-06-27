package com.eduhab.rest.controller;

import com.eduhab.rest.dto.auth.AuthRequest;
import com.eduhab.rest.dto.auth.AuthResponse;
import com.eduhab.service.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsValid() throws Exception {
        AuthResponse response = AuthResponse.builder()
                .userId(1).role("STUDENT").firstName("John").lastName("Doe")
                .email("john@test.com").token("mock-token").build();
        when(authService.login(any(AuthRequest.class))).thenReturn(response);

        AuthRequest request = new AuthRequest("john@test.com", "pass");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.token").value("mock-token"));
    }

    @Test
    void login_ShouldReturn401_WhenInvalid() throws Exception {
        when(authService.login(any(AuthRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        AuthRequest request = new AuthRequest("wrong@test.com", "badpass");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMe_ShouldReturnUser_WhenExists() throws Exception {
        AuthResponse response = AuthResponse.builder()
                .userId(1).role("TEACHER").firstName("Jane").lastName("Smith")
                .email("jane@test.com").build();
        when(authService.getUserById(1)).thenReturn(response);

        var auth = new UsernamePasswordAuthenticationToken(1, null,
                List.of(new SimpleGrantedAuthority("ROLE_TEACHER")));

        mockMvc.perform(get("/auth/me").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.role").value("TEACHER"));
    }

    @Test
    void getMe_ShouldReturn404_WhenNotFound() throws Exception {
        when(authService.getUserById(99)).thenReturn(null);

        var auth = new UsernamePasswordAuthenticationToken(99, null,
                List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));

        mockMvc.perform(get("/auth/me").with(authentication(auth)))
                .andExpect(status().isNotFound());
    }
}
