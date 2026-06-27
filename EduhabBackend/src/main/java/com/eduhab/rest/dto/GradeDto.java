package com.eduhab.rest.dto;

import com.eduhab.domain.Grade;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeDto {
    private int id;
    @NotNull
    private int studentId;
    @NotNull
    private int subjectId;
    @NotNull
    private int teacherId;
    @NotNull
    private Integer value;
    @NotBlank
    private String gradeType;
    private String comment;
    private Integer lessonId;
    private Integer weight;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer updatedBy;

    public static GradeDto toDto(Grade grade) {
        return GradeDto.builder()
                .id(grade.getId())
                .studentId(grade.getStudent().getId())
                .subjectId(grade.getSubject().getId())
                .teacherId(grade.getTeacher().getId())
                .value(grade.getValue())
                .gradeType(grade.getGradeType())
                .comment(grade.getComment())
                .lessonId(grade.getLesson() != null ? grade.getLesson().getId() : null)
                .weight(grade.getWeight())
                .createdAt(grade.getCreatedAt())
                .updatedAt(grade.getUpdatedAt())
                .updatedBy(grade.getUpdatedBy() != null ? grade.getUpdatedBy().getId() : null)
                .build();
    }
}
