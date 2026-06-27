package com.eduhab.rest.dto;

import com.eduhab.domain.Assignment;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDto {
    private int id;
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private int subjectId;
    @NotNull
    private int teacherId;
    @NotNull
    private int groupId;
    @NotNull
    private LocalDate assignedDate;
    private LocalDate dueDate;
    @NotBlank
    private String assignmentType;
    private String attachments;
    private Integer maxPoints;
    private LocalDateTime createdAt;

    public static AssignmentDto toDto(Assignment assignment) {
        return AssignmentDto.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .subjectId(assignment.getSubject().getId())
                .teacherId(assignment.getTeacher().getId())
                .groupId(assignment.getGroup().getId())
                .assignedDate(assignment.getAssignedDate())
                .dueDate(assignment.getDueDate())
                .assignmentType(assignment.getAssignmentType())
                .attachments(assignment.getAttachments())
                .maxPoints(assignment.getMaxPoints())
                .createdAt(assignment.getCreatedAt())
                .build();
    }
}
