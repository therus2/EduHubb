package com.eduhab.rest.controller;

import com.eduhab.domain.Assignment;
import com.eduhab.domain.HomeworkCompletion;
import com.eduhab.rest.dto.AssignmentDto;
import com.eduhab.rest.dto.response.AssignmentResponse;
import com.eduhab.rest.dto.response.AssignmentStatsResponse;
import com.eduhab.rest.mapper.AssignmentMapper;
import com.eduhab.service.AssignmentService;
import com.eduhab.service.HomeworkCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final AssignmentMapper assignmentMapper;
    private final HomeworkCompletionService homeworkCompletionService;

    @GetMapping("/assignment")
    public List<AssignmentResponse> getAll(
            @RequestParam(required = false) Integer groupId,
            @RequestParam(required = false) Integer teacherId) {
        List<Assignment> assignments;
        if (groupId != null && teacherId != null) {
            assignments = assignmentService.getByGroupIdAndTeacherId(groupId, teacherId);
        } else if (groupId != null) {
            assignments = assignmentService.getByGroupId(groupId);
        } else if (teacherId != null) {
            assignments = assignmentService.getByTeacherId(teacherId);
        } else {
            assignments = assignmentService.getAll();
        }
        return assignments.stream().map(this::toAssignmentResponse).collect(Collectors.toList());
    }

    @GetMapping("/assignment/{id}")
    public AssignmentResponse getById(@PathVariable int id) {
        return toAssignmentResponse(assignmentService.getById(id));
    }

    @PostMapping("/assignment")
    public AssignmentDto insert(@Valid @RequestBody AssignmentDto dto) {
        return AssignmentDto.toDto(assignmentService.insert(assignmentMapper.toEntity(dto)));
    }

    @PutMapping("/assignment/{id}")
    public AssignmentDto update(@PathVariable int id, @Valid @RequestBody AssignmentDto dto) {
        var entity = assignmentMapper.toEntity(dto);
        entity.setId(id);
        return AssignmentDto.toDto(assignmentService.update(entity));
    }

    @DeleteMapping("/assignment/{id}")
    public void deleteById(@PathVariable int id) {
        assignmentService.deleteById(id);
    }

    @GetMapping("/assignment/{id}/stats")
    public AssignmentStatsResponse getStats(@PathVariable int id) {
        Assignment assignment = assignmentService.getById(id);
        List<HomeworkCompletion> completions = homeworkCompletionService.getByAssignmentId(id);
        int total = completions.size();
        long submitted = completions.stream().filter(h -> "SUBMITTED".equals(h.getStatus())).count();
        long overdue = completions.stream().filter(h -> "OVERDUE".equals(h.getStatus())).count();
        long onReview = completions.stream().filter(h -> "ON_REVIEW".equals(h.getStatus())).count();
        String teacherName = assignment.getTeacher().getUser().getLastName() + " "
                + assignment.getTeacher().getUser().getFirstName().charAt(0) + ".";

        return AssignmentStatsResponse.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .subjectName(assignment.getSubject().getName())
                .groupCode(assignment.getGroup().getCode())
                .dueDate(assignment.getDueDate() != null ? assignment.getDueDate().toString() : null)
                .total(total)
                .submitted((int) submitted)
                .overdueCount((int) overdue)
                .onReview((int) onReview)
                .build();
    }

    private AssignmentResponse toAssignmentResponse(Assignment assignment) {
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .subjectName(assignment.getSubject().getName())
                .groupCode(assignment.getGroup().getCode())
                .assignedDate(assignment.getAssignedDate())
                .dueDate(assignment.getDueDate())
                .assignmentType(assignment.getAssignmentType())
                .attachments(assignment.getAttachments())
                .maxPoints(assignment.getMaxPoints())
                .subjectId(assignment.getSubject().getId())
                .teacherId(assignment.getTeacher().getId())
                .groupId(assignment.getGroup().getId())
                .build();
    }
}
