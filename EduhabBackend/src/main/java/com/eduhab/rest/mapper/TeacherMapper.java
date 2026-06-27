package com.eduhab.rest.mapper;

import com.eduhab.domain.Teacher;
import com.eduhab.rest.dto.TeacherDto;
import com.eduhab.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeacherMapper {
    private final UserService userService;

    public Teacher toEntity(TeacherDto dto) {
        Teacher teacher = new Teacher();
        teacher.setUser(userService.getById(dto.getUser().getId()));
        teacher.setEmployeeId(dto.getEmployeeId());
        teacher.setDepartment(dto.getDepartment());
        return teacher;
    }
}
