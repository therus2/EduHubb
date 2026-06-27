package com.eduhab.rest.mapper;

import com.eduhab.domain.Assignment;
import com.eduhab.rest.dto.AssignmentDto;
import com.eduhab.service.StudentGroupService;
import com.eduhab.service.SubjectService;
import com.eduhab.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignmentMapper {
    private final SubjectService subjectService;
    private final TeacherService teacherService;
    private final StudentGroupService studentGroupService;

    public Assignment toEntity(AssignmentDto dto) {
        Assignment assignment = new Assignment();
        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setSubject(subjectService.getById(dto.getSubjectId()));
        assignment.setTeacher(teacherService.getById(dto.getTeacherId()));
        assignment.setGroup(studentGroupService.getById(dto.getGroupId()));
        assignment.setAssignedDate(dto.getAssignedDate());
        assignment.setDueDate(dto.getDueDate());
        assignment.setAssignmentType(dto.getAssignmentType());
        assignment.setAttachments(dto.getAttachments());
        assignment.setMaxPoints(dto.getMaxPoints());
        assignment.setCreatedAt(dto.getCreatedAt());
        return assignment;
    }
}
