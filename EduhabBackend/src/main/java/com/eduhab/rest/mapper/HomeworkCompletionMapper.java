package com.eduhab.rest.mapper;

import com.eduhab.domain.HomeworkCompletion;
import com.eduhab.rest.dto.HomeworkCompletionDto;
import com.eduhab.service.AssignmentService;
import com.eduhab.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HomeworkCompletionMapper {
    private final AssignmentService assignmentService;
    private final StudentService studentService;

    public HomeworkCompletion toEntity(HomeworkCompletionDto dto) {
        HomeworkCompletion hc = new HomeworkCompletion();
        hc.setAssignment(assignmentService.getById(dto.getAssignmentId()));
        hc.setStudent(studentService.getById(dto.getStudentId()));
        hc.setStatus(dto.getStatus());
        hc.setStudentComment(dto.getStudentComment());
        hc.setAttachments(dto.getAttachments());
        hc.setSubmittedAt(dto.getSubmittedAt());
        hc.setReceivedPoints(dto.getReceivedPoints());
        hc.setGradedAt(dto.getGradedAt());
        return hc;
    }
}
