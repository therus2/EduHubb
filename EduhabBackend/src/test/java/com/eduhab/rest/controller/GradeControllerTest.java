package com.eduhab.rest.controller;

import com.eduhab.config.jwt.JwtUtil;
import com.eduhab.domain.*;
import com.eduhab.rest.dto.GradeDto;
import com.eduhab.rest.mapper.GradeMapper;
import com.eduhab.service.GradeService;
import com.eduhab.service.StudentService;
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

@WebMvcTest(GradeController.class)
@AutoConfigureMockMvc(addFilters = false)
class GradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GradeService gradeService;

    @MockBean
    private GradeMapper gradeMapper;

    @MockBean
    private StudentService studentService;

    @MockBean
    private JwtUtil jwtUtil;

    private Grade createGrade(int id, int value) {
        User tu = User.builder().id(10).firstName("Tea").lastName("Cher").role("TEACHER").build();
        Teacher teacher = Teacher.builder().id(20).user(tu).build();
        Subject subject = Subject.builder().id(30).name("Math").build();
        User su = User.builder().id(40).firstName("Stu").lastName("Dent").role("STUDENT").build();
        StudentGroup group = StudentGroup.builder().id(50).name("10A").code("10A").courseNumber(10).build();
        Student student = Student.builder().id(60).user(su).group(group).build();
        Lesson lesson = Lesson.builder().id(70)
                .group(group).subject(subject).teacher(teacher)
                .dayOfWeek(1)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 45))
                .build();

        Grade g = new Grade();
        g.setId(id);
        g.setValue(value);
        g.setGradeType("CURRENT");
        g.setWeight(1);
        g.setStudent(student);
        g.setSubject(subject);
        g.setTeacher(teacher);
        g.setLesson(lesson);
        return g;
    }

    @Test
    void getAll_ShouldReturnGrades() throws Exception {
        when(gradeService.getAll()).thenReturn(List.of(createGrade(1, 4)));

        mockMvc.perform(get("/grade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].gradeId").value(1))
                .andExpect(jsonPath("$[0].value").value(4));
    }

    @Test
    void getAll_WithStudentIdParam_ShouldFilter() throws Exception {
        when(gradeService.getByStudentId(60)).thenReturn(List.of(createGrade(2, 5)));

        mockMvc.perform(get("/grade").param("studentId", "60"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getById_ShouldReturnGrade() throws Exception {
        when(gradeService.getById(1)).thenReturn(createGrade(1, 3));

        mockMvc.perform(get("/grade/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gradeId").value(1));
    }

    @Test
    void insert_ShouldReturnGrade() throws Exception {
        GradeDto dto = GradeDto.builder()
                .studentId(60).subjectId(30).teacherId(20)
                .value(5).gradeType("CURRENT")
                .build();
        Grade entity = createGrade(1, 5);
        when(gradeMapper.toEntity(any(GradeDto.class))).thenReturn(entity);
        when(gradeService.insert(any(Grade.class))).thenReturn(entity);

        mockMvc.perform(post("/grade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(5));
    }

    @Test
    void insert_ShouldReturn400_WhenValidationFails() throws Exception {
        GradeDto dto = GradeDto.builder()
                .value(5).gradeType("") // @NotBlank on gradeType should fail
                .build();

        mockMvc.perform(post("/grade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteById_ShouldReturn200() throws Exception {
        mockMvc.perform(delete("/grade/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAverages_ShouldReturnList() throws Exception {
        when(gradeService.getByStudentId(60)).thenReturn(List.of(createGrade(1, 4), createGrade(2, 5)));

        mockMvc.perform(get("/grade/averages").param("studentId", "60"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
}
