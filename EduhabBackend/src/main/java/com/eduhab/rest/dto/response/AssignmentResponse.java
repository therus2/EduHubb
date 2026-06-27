package com.eduhab.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResponse {
    private int id;
    private String title;
    private String description;
    private String subjectName;
    private String groupCode;
    private LocalDate assignedDate;
    private LocalDate dueDate;
    private String assignmentType;
    private String attachments;
    private Integer maxPoints;
    private int subjectId;
    private int teacherId;
    private int groupId;
}
