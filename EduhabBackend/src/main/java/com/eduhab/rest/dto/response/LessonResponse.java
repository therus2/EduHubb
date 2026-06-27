package com.eduhab.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponse {
    private int lessonId;
    private String subjectName;
    private String teacherName;
    private String groupCode;
    private int groupId;
    private int subjectId;
    private int teacherId;
    private Integer dayOfWeek;
    private String startTime;
    private String endTime;
    private String classroom;
    private String lessonType;
    private Integer weekNumber;
    private Boolean isAlternatingWeek;
    private String lessonTopic;
    private String info;
}
