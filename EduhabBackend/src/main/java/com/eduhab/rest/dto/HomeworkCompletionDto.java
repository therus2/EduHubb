package com.eduhab.rest.dto;

import com.eduhab.domain.HomeworkCompletion;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeworkCompletionDto {
    private int id;
    @NotNull
    private int assignmentId;
    @NotNull
    private int studentId;
    @NotBlank
    private String status;
    private String studentComment;
    private String attachments;
    private LocalDateTime submittedAt;
    private Integer receivedPoints;
    private LocalDateTime gradedAt;

    public static HomeworkCompletionDto toDto(HomeworkCompletion hc) {
        return HomeworkCompletionDto.builder()
                .id(hc.getId())
                .assignmentId(hc.getAssignment().getId())
                .studentId(hc.getStudent().getId())
                .status(hc.getStatus())
                .studentComment(hc.getStudentComment())
                .attachments(hc.getAttachments())
                .submittedAt(hc.getSubmittedAt())
                .receivedPoints(hc.getReceivedPoints())
                .gradedAt(hc.getGradedAt())
                .build();
    }
}
