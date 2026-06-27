package com.eduhab.rest.controller;

import com.eduhab.domain.HomeworkCompletion;
import com.eduhab.rest.dto.HomeworkCompletionDto;
import com.eduhab.rest.dto.response.SubmissionResponse;
import com.eduhab.rest.mapper.HomeworkCompletionMapper;
import com.eduhab.service.HomeworkCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class HomeworkCompletionController {
    private final HomeworkCompletionService homeworkCompletionService;
    private final HomeworkCompletionMapper homeworkCompletionMapper;

    @GetMapping("/homework-completion")
    public List<SubmissionResponse> getAll(
            @RequestParam(required = false) Integer assignmentId,
            @RequestParam(required = false) Integer studentId) {
        List<HomeworkCompletion> completions;
        if (assignmentId != null) {
            completions = homeworkCompletionService.getByAssignmentId(assignmentId);
        } else if (studentId != null) {
            completions = homeworkCompletionService.getByStudentId(studentId);
        } else {
            completions = homeworkCompletionService.getAll();
        }
        return completions.stream().map(this::toSubmissionResponse).collect(Collectors.toList());
    }

    @GetMapping("/homework-completion/{id}")
    public SubmissionResponse getById(@PathVariable int id) {
        return toSubmissionResponse(homeworkCompletionService.getById(id));
    }

    @PostMapping("/homework-completion")
    public HomeworkCompletionDto insert(@Valid @RequestBody HomeworkCompletionDto dto) {
        return HomeworkCompletionDto.toDto(homeworkCompletionService.insert(homeworkCompletionMapper.toEntity(dto)));
    }

    @PutMapping("/homework-completion/{id}")
    public HomeworkCompletionDto update(@PathVariable int id, @Valid @RequestBody HomeworkCompletionDto dto) {
        var entity = homeworkCompletionMapper.toEntity(dto);
        entity.setId(id);
        return HomeworkCompletionDto.toDto(homeworkCompletionService.update(entity));
    }

    @DeleteMapping("/homework-completion/{id}")
    public void deleteById(@PathVariable int id) {
        homeworkCompletionService.deleteById(id);
    }

    private SubmissionResponse toSubmissionResponse(HomeworkCompletion hc) {
        String name = hc.getStudent().getUser().getLastName() + " "
                + hc.getStudent().getUser().getFirstName().charAt(0) + "."
                + (hc.getStudent().getUser().getPatronymic() != null
                    ? hc.getStudent().getUser().getPatronymic().charAt(0) + "." : "");
        return SubmissionResponse.builder()
                .completionId(hc.getId())
                .assignmentId(hc.getAssignment().getId())
                .studentId(hc.getStudent().getId())
                .studentName(name)
                .status(hc.getStatus())
                .submittedAt(hc.getSubmittedAt())
                .isSubmitted("SUBMITTED".equals(hc.getStatus()) || "ON_REVIEW".equals(hc.getStatus()))
                .receivedPoints(hc.getReceivedPoints())
                .studentComment(hc.getStudentComment())
                .build();
    }
}
