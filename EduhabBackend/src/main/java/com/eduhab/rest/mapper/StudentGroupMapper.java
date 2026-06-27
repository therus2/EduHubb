package com.eduhab.rest.mapper;

import com.eduhab.domain.StudentGroup;
import com.eduhab.rest.dto.StudentGroupDto;
import org.springframework.stereotype.Component;

@Component
public class StudentGroupMapper {
    public StudentGroup toEntity(StudentGroupDto dto) {
        return StudentGroup.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .courseNumber(dto.getCourseNumber())
                .specialization(dto.getSpecialization())
                .maxStudents(dto.getMaxStudents())
                .build();
    }
}
