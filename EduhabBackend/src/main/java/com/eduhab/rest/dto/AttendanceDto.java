package com.eduhab.rest.dto;

import com.eduhab.domain.Attendance;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDto {
    private int id;
    @NotNull
    private int studentId;
    private Integer lessonId;
    @NotNull
    private LocalDate date;
    @NotBlank
    private String status;
    private String comment;
    private LocalDateTime markedAt;
    private Integer markedBy;

    public static AttendanceDto toDto(Attendance attendance) {
        return AttendanceDto.builder()
                .id(attendance.getId())
                .studentId(attendance.getStudent().getId())
                .lessonId(attendance.getLesson() != null ? attendance.getLesson().getId() : null)
                .date(attendance.getDate())
                .status(attendance.getStatus())
                .comment(attendance.getComment())
                .markedAt(attendance.getMarkedAt())
                .markedBy(attendance.getMarkedBy() != null ? attendance.getMarkedBy().getId() : null)
                .build();
    }
}
