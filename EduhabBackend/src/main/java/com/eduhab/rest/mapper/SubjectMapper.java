package com.eduhab.rest.mapper;

import com.eduhab.domain.Subject;
import com.eduhab.rest.dto.SubjectDto;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {
    public Subject toEntity(SubjectDto dto) {
        return Subject.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .totalHours(dto.getTotalHours())
                .credits(dto.getCredits())
                .isExam(dto.getIsExam())
                .isCredit(dto.getIsCredit())
                .build();
    }
}
