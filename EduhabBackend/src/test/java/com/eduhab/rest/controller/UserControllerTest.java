package com.eduhab.rest.controller;

import com.eduhab.config.jwt.JwtUtil;
import com.eduhab.domain.User;
import com.eduhab.rest.dto.UserDto;
import com.eduhab.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void getAll_ShouldReturnList() throws Exception {
        User user = User.builder().id(1).email("test@test.com")
                .firstName("John").lastName("Doe").role("STUDENT").build();
        when(userService.getAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].email").value("test@test.com"));
    }

    @Test
    void getById_ShouldReturnUser() throws Exception {
        User user = User.builder().id(1).email("test@test.com")
                .firstName("John").lastName("Doe").role("STUDENT").build();
        when(userService.getById(1)).thenReturn(user);

        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void insert_ShouldReturnCreatedUser() throws Exception {
        UserDto dto = UserDto.builder()
                .email("new@test.com").passwordHash("pass")
                .firstName("New").lastName("User").role("TEACHER")
                .build();
        User saved = User.builder().id(2).email("new@test.com")
                .firstName("New").lastName("User").role("TEACHER").build();
        when(userService.insert(any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void insert_ShouldReturn400_WhenValidationFails() throws Exception {
        UserDto dto = UserDto.builder()
                .email("") // @NotBlank should fail
                .firstName("New").lastName("User").role("")
                .build();

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldReturnUpdatedUser() throws Exception {
        UserDto dto = UserDto.builder()
                .email("update@test.com").passwordHash("newpass")
                .firstName("Up").lastName("Date").role("STUDENT")
                .build();
        User updated = User.builder().id(1).email("update@test.com")
                .firstName("Up").lastName("Date").role("STUDENT").build();
        when(userService.update(any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("update@test.com"));
    }

    @Test
    void deleteById_ShouldReturn200() throws Exception {
        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isOk());
    }
}
