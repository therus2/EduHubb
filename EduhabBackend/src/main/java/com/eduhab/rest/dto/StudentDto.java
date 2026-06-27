package com.eduhab.rest.dto;

import com.eduhab.domain.Student;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {
    private int id;
    private UserDto user;
    private Integer groupId;
    private LocalDate enrollmentDate;
    private String studentIdNumber;

    public static StudentDto toDto(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .user(UserDto.toDto(student.getUser()))
                .groupId(student.getGroup() != null ? student.getGroup().getId() : null)
                .enrollmentDate(student.getEnrollmentDate())
                .studentIdNumber(student.getStudentIdNumber())
                .build();
    }
}
