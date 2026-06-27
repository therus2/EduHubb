package com.eduhab.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalGradeResponse {
    private int studentId;
    private String studentName;
    private String averageMark;
    private List<Integer> marks;
    private List<Integer> gradeIds;
}
