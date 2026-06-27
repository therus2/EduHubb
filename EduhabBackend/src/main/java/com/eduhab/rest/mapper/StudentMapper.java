package com.eduhab.rest.mapper;

import com.eduhab.domain.Student;
import com.eduhab.rest.dto.StudentDto;
import com.eduhab.service.StudentGroupService;
import com.eduhab.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentMapper {
    private final UserService userService;
    private final StudentGroupService studentGroupService;

    public Student toEntity(StudentDto dto) {
        Student student = new Student();
        student.setUser(userService.getById(dto.getUser().getId()));
        student.setGroup(dto.getGroupId() != null ? studentGroupService.getById(dto.getGroupId()) : null);
        student.setEnrollmentDate(dto.getEnrollmentDate());
        student.setStudentIdNumber(dto.getStudentIdNumber());
        return student;
    }
}
