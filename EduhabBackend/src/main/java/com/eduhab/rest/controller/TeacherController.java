package com.eduhab.rest.controller;

import com.eduhab.domain.Lesson;
import com.eduhab.domain.StudentGroup;
import com.eduhab.domain.Subject;
import com.eduhab.rest.dto.TeacherDto;
import com.eduhab.rest.dto.response.ClassResponse;
import com.eduhab.rest.dto.response.SubjectResponse;
import com.eduhab.rest.mapper.TeacherMapper;
import com.eduhab.service.LessonService;
import com.eduhab.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;
    private final TeacherMapper teacherMapper;
    private final LessonService lessonService;

    @GetMapping("/teacher")
    public List<TeacherDto> getAll() {
        return teacherService.getAll().stream().map(TeacherDto::toDto).collect(Collectors.toList());
    }

    @GetMapping("/teacher/{id}")
    public TeacherDto getById(@PathVariable int id) {
        return TeacherDto.toDto(teacherService.getById(id));
    }

    @PostMapping("/teacher")
    public TeacherDto insert(@Valid @RequestBody TeacherDto dto) {
        return TeacherDto.toDto(teacherService.insert(teacherMapper.toEntity(dto)));
    }

    @PutMapping("/teacher/{id}")
    public TeacherDto update(@PathVariable int id, @Valid @RequestBody TeacherDto dto) {
        var entity = teacherMapper.toEntity(dto);
        entity.setId(id);
        return TeacherDto.toDto(teacherService.update(entity));
    }

    @DeleteMapping("/teacher/{id}")
    public void deleteById(@PathVariable int id) {
        teacherService.deleteById(id);
    }

    @GetMapping("/teacher/{id}/groups")
    public List<ClassResponse> getGroups(@PathVariable int id) {
        List<Lesson> lessons = lessonService.getByTeacherId(id);
        Map<Integer, StudentGroup> uniqueGroups = lessons.stream()
                .map(Lesson::getGroup)
                .distinct()
                .collect(Collectors.toMap(StudentGroup::getId, g -> g, (a, b) -> a));
        return uniqueGroups.values().stream()
                .map(g -> ClassResponse.builder()
                        .id(g.getId())
                        .className(g.getName())
                        .code(g.getCode())
                        .courseNumber(g.getCourseNumber())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/teacher/{id}/subjects")
    public List<SubjectResponse> getSubjects(@PathVariable int id) {
        List<Lesson> lessons = lessonService.getByTeacherId(id);
        Map<Integer, Subject> uniqueSubjects = lessons.stream()
                .map(Lesson::getSubject)
                .distinct()
                .collect(Collectors.toMap(Subject::getId, s -> s, (a, b) -> a));
        return uniqueSubjects.values().stream()
                .map(s -> SubjectResponse.builder()
                        .id(s.getId())
                        .subjectName(s.getName())
                        .code(s.getCode())
                        .description(s.getDescription())
                        .totalHours(s.getTotalHours())
                        .credits(s.getCredits())
                        .build())
                .collect(Collectors.toList());
    }
}
