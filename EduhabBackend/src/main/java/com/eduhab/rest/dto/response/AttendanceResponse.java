package com.eduhab.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {
    private int id;
    private String className;
    private String date;
    private String status;
    private String statusText;
    private String comment;
    private String lessonTopic;
    private int studentId;
    private Integer lessonId;
}
