package com.eduhab.rest.mapper;

import com.eduhab.domain.Attendance;
import com.eduhab.rest.dto.AttendanceDto;
import com.eduhab.service.LessonService;
import com.eduhab.service.StudentService;
import com.eduhab.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttendanceMapper {
    private final StudentService studentService;
    private final LessonService lessonService;
    private final TeacherService teacherService;

    public Attendance toEntity(AttendanceDto dto) {
        Attendance attendance = new Attendance();
        attendance.setStudent(studentService.getById(dto.getStudentId()));
        attendance.setLesson(dto.getLessonId() != null ? lessonService.getById(dto.getLessonId()) : null);
        attendance.setDate(dto.getDate());
        attendance.setStatus(dto.getStatus());
        attendance.setComment(dto.getComment());
        attendance.setMarkedAt(dto.getMarkedAt());
        attendance.setMarkedBy(dto.getMarkedBy() != null ? teacherService.getById(dto.getMarkedBy()) : null);
        return attendance;
    }
}
