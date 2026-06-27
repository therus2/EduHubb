package com.eduhab.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentStatsResponse {
    private int id;
    private String title;
    private String description;
    private String subjectName;
    private String groupCode;
    private String dueDate;
    private int total;
    private int submitted;
    private int overdueCount;
    private int onReview;
}
