package com.eduhab.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentInfoResponse {
    private int studentId;
    private String studentName;
    private String phoneNumber;
    private String groupCode;
    private Integer groupId;
}
