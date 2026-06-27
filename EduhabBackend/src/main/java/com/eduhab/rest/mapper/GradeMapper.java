package com.eduhab.rest.mapper;

import com.eduhab.domain.Grade;
import com.eduhab.rest.dto.GradeDto;
import com.eduhab.service.LessonService;
import com.eduhab.service.StudentService;
import com.eduhab.service.SubjectService;
import com.eduhab.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GradeMapper {
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final TeacherService teacherService;
    private final LessonService lessonService;

    public Grade toEntity(GradeDto dto) {
        Grade grade = new Grade();
        grade.setStudent(studentService.getById(dto.getStudentId()));
        grade.setSubject(subjectService.getById(dto.getSubjectId()));
        grade.setTeacher(teacherService.getById(dto.getTeacherId()));
        grade.setValue(dto.getValue());
        grade.setGradeType(dto.getGradeType());
        grade.setComment(dto.getComment());
        grade.setLesson(dto.getLessonId() != null ? lessonService.getById(dto.getLessonId()) : null);
        grade.setWeight(dto.getWeight());
        grade.setCreatedAt(dto.getCreatedAt());
        grade.setUpdatedAt(dto.getUpdatedAt());
        grade.setUpdatedBy(dto.getUpdatedBy() != null ? teacherService.getById(dto.getUpdatedBy()) : null);
        return grade;
    }
}
