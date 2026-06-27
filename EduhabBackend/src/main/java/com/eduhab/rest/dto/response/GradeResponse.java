package com.eduhab.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeResponse {
    private int gradeId;
    private int value;
    private String gradeType;
    private int weight;
    private String comment;
    private String teacherName;
    private String subjectName;
    private String createdAt;
    private Integer lessonId;
    private int studentId;
    private int subjectId;
    private int teacherId;
}
