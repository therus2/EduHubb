package com.eduhab.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectAverageResponse {
    private int subjectId;
    private String subjectName;
    private double average;
    private int progress;
    private String statusText;
    private String teacherName;
    private int gradeCount;
}
