package com.eduhab.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponse {
    private int completionId;
    private int assignmentId;
    private int studentId;
    private String studentName;
    private String status;
    private LocalDateTime submittedAt;
    private boolean isSubmitted;
    private Integer receivedPoints;
    private String studentComment;
}
