package com.eduhab.rest.controller;

import com.eduhab.config.jwt.JwtUtil;
import com.eduhab.domain.*;
import com.eduhab.rest.dto.LessonDto;
import com.eduhab.rest.mapper.LessonMapper;
import com.eduhab.service.LessonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LessonController.class)
@AutoConfigureMockMvc(addFilters = false)
class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LessonService lessonService;

    @MockBean
    private LessonMapper lessonMapper;

    @MockBean
    private JwtUtil jwtUtil;

    private Lesson createLesson(int id) {
        User tu = User.builder().id(10).firstName("Tea").lastName("Cher").patronymic("P.").role("TEACHER").build();
        Teacher teacher = Teacher.builder().id(20).user(tu).build();
        Subject subject = Subject.builder().id(30).name("Physics").build();
        StudentGroup group = StudentGroup.builder().id(40).name("10A").code("10A").courseNumber(10).build();

        return Lesson.builder()
                .id(id).group(group).subject(subject).teacher(teacher)
                .dayOfWeek(1)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(8, 45))
                .classroom("101")
                .lessonType("lecture")
                .build();
    }

    @Test
    void getAll_ShouldReturnLessons() throws Exception {
        when(lessonService.getAll()).thenReturn(List.of(createLesson(1)));

        mockMvc.perform(get("/lesson"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].lessonId").value(1));
    }

    @Test
    void getAll_WithGroupIdParam_ShouldFilter() throws Exception {
        when(lessonService.getByGroupId(40)).thenReturn(List.of(createLesson(2)));

        mockMvc.perform(get("/lesson").param("groupId", "40"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getAll_WithGroupAndDay_ShouldFilter() throws Exception {
        when(lessonService.getByGroupIdAndDayOfWeek(40, 1)).thenReturn(List.of(createLesson(3)));

        mockMvc.perform(get("/lesson")
                        .param("groupId", "40")
                        .param("dayOfWeek", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getById_ShouldReturnLesson() throws Exception {
        when(lessonService.getById(1)).thenReturn(createLesson(1));

        mockMvc.perform(get("/lesson/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectName").value("Physics"));
    }

    @Test
    void insert_ShouldReturnLesson() throws Exception {
        LessonDto dto = LessonDto.builder()
                .groupId(40).subjectId(30).teacherId(20)
                .dayOfWeek(1)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(8, 45))
                .build();
        Lesson entity = createLesson(1);
        when(lessonMapper.toEntity(any(LessonDto.class))).thenReturn(entity);
        when(lessonService.insert(any(Lesson.class))).thenReturn(entity);

        mockMvc.perform(post("/lesson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void insert_ShouldReturn400_WhenValidationFails() throws Exception {
        LessonDto dto = LessonDto.builder()
                .groupId(40).subjectId(30)
                .dayOfWeek(null) // @NotNull should fail
                .build();

        mockMvc.perform(post("/lesson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteById_ShouldReturn200() throws Exception {
        mockMvc.perform(delete("/lesson/1"))
                .andExpect(status().isOk());
    }
}
