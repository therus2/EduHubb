package com.eduhab.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectResponse {
    private int id;
    private String subjectName;
    private String code;
    private String description;
    private Integer totalHours;
    private Integer credits;
}
