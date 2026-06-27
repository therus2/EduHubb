package com.eduhab.rest.dto;

import com.eduhab.domain.Subject;
import lombok.*;
import javax.validation.constraints.NotBlank;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDto {
    private int id;
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private String description;
    private Integer totalHours;
    private Integer credits;
    private Boolean isExam;
    private Boolean isCredit;

    public static SubjectDto toDto(Subject subject) {
        return SubjectDto.builder()
                .id(subject.getId())
                .code(subject.getCode())
                .name(subject.getName())
                .description(subject.getDescription())
                .totalHours(subject.getTotalHours())
                .credits(subject.getCredits())
                .isExam(subject.getIsExam())
                .isCredit(subject.getIsCredit())
                .build();
    }
}
