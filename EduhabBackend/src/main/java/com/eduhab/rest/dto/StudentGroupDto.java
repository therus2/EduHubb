package com.eduhab.rest.dto;

import com.eduhab.domain.StudentGroup;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGroupDto {
    private int id;
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    @NotNull
    private Integer courseNumber;
    private String specialization;
    private Integer maxStudents;
    private LocalDateTime createdAt;

    public static StudentGroupDto toDto(StudentGroup group) {
        return StudentGroupDto.builder()
                .id(group.getId())
                .code(group.getCode())
                .name(group.getName())
                .courseNumber(group.getCourseNumber())
                .specialization(group.getSpecialization())
                .maxStudents(group.getMaxStudents())
                .createdAt(group.getCreatedAt())
                .build();
    }
}
