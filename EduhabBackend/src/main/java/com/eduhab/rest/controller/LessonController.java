package com.eduhab.rest.controller;

import com.eduhab.domain.Lesson;
import com.eduhab.rest.dto.LessonDto;
import com.eduhab.rest.dto.response.LessonResponse;
import com.eduhab.rest.mapper.LessonMapper;
import com.eduhab.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;
    private final LessonMapper lessonMapper;

    @GetMapping("/lesson")
    public List<LessonResponse> getAll(
            @RequestParam(required = false) Integer groupId,
            @RequestParam(required = false) Integer teacherId,
            @RequestParam(required = false) Integer dayOfWeek) {
        List<Lesson> lessons;
        if (groupId != null && dayOfWeek != null) {
            lessons = lessonService.getByGroupIdAndDayOfWeek(groupId, dayOfWeek);
        } else if (groupId != null && teacherId != null) {
            lessons = lessonService.getByGroupIdAndTeacherId(groupId, teacherId);
        } else if (teacherId != null && dayOfWeek != null) {
            lessons = lessonService.getByTeacherIdAndDayOfWeek(teacherId, dayOfWeek);
        } else if (groupId != null) {
            lessons = lessonService.getByGroupId(groupId);
        } else if (teacherId != null) {
            lessons = lessonService.getByTeacherId(teacherId);
        } else {
            lessons = lessonService.getAll();
        }
        return lessons.stream().map(this::toLessonResponse).collect(Collectors.toList());
    }

    @GetMapping("/lesson/{id}")
    public LessonResponse getById(@PathVariable int id) {
        return toLessonResponse(lessonService.getById(id));
    }

    @PostMapping("/lesson")
    public LessonDto insert(@Valid @RequestBody LessonDto dto) {
        return LessonDto.toDto(lessonService.insert(lessonMapper.toEntity(dto)));
    }

    @PutMapping("/lesson/{id}")
    public LessonDto update(@PathVariable int id, @Valid @RequestBody LessonDto dto) {
        var entity = lessonMapper.toEntity(dto);
        entity.setId(id);
        return LessonDto.toDto(lessonService.update(entity));
    }

    @DeleteMapping("/lesson/{id}")
    public void deleteById(@PathVariable int id) {
        lessonService.deleteById(id);
    }

    private LessonResponse toLessonResponse(Lesson lesson) {
        String teacherName = lesson.getTeacher().getUser().getLastName() + " "
                + lesson.getTeacher().getUser().getFirstName().charAt(0) + "."
                + (lesson.getTeacher().getUser().getPatronymic() != null
                    ? lesson.getTeacher().getUser().getPatronymic().charAt(0) + "." : "");
        return LessonResponse.builder()
                .lessonId(lesson.getId())
                .subjectName(lesson.getSubject().getName())
                .teacherName(teacherName)
                .groupCode(lesson.getGroup().getCode())
                .groupId(lesson.getGroup().getId())
                .subjectId(lesson.getSubject().getId())
                .teacherId(lesson.getTeacher().getId())
                .dayOfWeek(lesson.getDayOfWeek())
                .startTime(lesson.getStartTime().toString())
                .endTime(lesson.getEndTime().toString())
                .classroom(lesson.getClassroom())
                .lessonType(lesson.getLessonType())
                .weekNumber(lesson.getWeekNumber())
                .isAlternatingWeek(lesson.getIsAlternatingWeek())
                .lessonTopic(lesson.getLessonTopic())
                .info(lesson.getStartTime().toString().substring(0, 5) + " - " + lesson.getEndTime().toString().substring(0, 5))
                .build();
    }
}
