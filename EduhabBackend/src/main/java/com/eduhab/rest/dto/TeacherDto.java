package com.eduhab.rest.dto;

import com.eduhab.domain.Teacher;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherDto {
    private int id;
    private UserDto user;
    private String employeeId;
    private String department;

    public static TeacherDto toDto(Teacher teacher) {
        return TeacherDto.builder()
                .id(teacher.getId())
                .user(UserDto.toDto(teacher.getUser()))
                .employeeId(teacher.getEmployeeId())
                .department(teacher.getDepartment())
                .build();
    }
}

