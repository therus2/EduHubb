package com.eduhab.rest.mapper;

import com.eduhab.domain.Lesson;
import com.eduhab.rest.dto.LessonDto;
import com.eduhab.service.StudentGroupService;
import com.eduhab.service.SubjectService;
import com.eduhab.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LessonMapper {
    private final StudentGroupService studentGroupService;
    private final SubjectService subjectService;
    private final TeacherService teacherService;

    public Lesson toEntity(LessonDto dto) {
        Lesson lesson = new Lesson();
        lesson.setGroup(studentGroupService.getById(dto.getGroupId()));
        lesson.setSubject(subjectService.getById(dto.getSubjectId()));
        lesson.setTeacher(teacherService.getById(dto.getTeacherId()));
        lesson.setDayOfWeek(dto.getDayOfWeek());
        lesson.setStartTime(dto.getStartTime());
        lesson.setEndTime(dto.getEndTime());
        lesson.setClassroom(dto.getClassroom());
        lesson.setLessonType(dto.getLessonType());
        lesson.setWeekNumber(dto.getWeekNumber());
        lesson.setIsAlternatingWeek(dto.getIsAlternatingWeek());
        lesson.setLessonTopic(dto.getLessonTopic());
        return lesson;
    }
}
