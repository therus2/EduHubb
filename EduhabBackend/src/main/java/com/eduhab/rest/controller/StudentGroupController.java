package com.eduhab.rest.controller;

import com.eduhab.domain.StudentGroup;
import com.eduhab.rest.dto.StudentGroupDto;
import com.eduhab.rest.dto.response.ClassResponse;
import com.eduhab.rest.mapper.StudentGroupMapper;
import com.eduhab.service.StudentGroupService;
import com.eduhab.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class StudentGroupController {
    private final StudentGroupService studentGroupService;
    private final StudentGroupMapper studentGroupMapper;
    private final StudentService studentService;

    @GetMapping("/group")
    public List<ClassResponse> getAll() {
        return studentGroupService.getAll().stream()
                .map(this::toClassResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/group/{id}")
    public StudentGroupDto getById(@PathVariable int id) {
        return StudentGroupDto.toDto(studentGroupService.getById(id));
    }

    @PostMapping("/group")
    public StudentGroupDto insert(@Valid @RequestBody StudentGroupDto dto) {
        return StudentGroupDto.toDto(studentGroupService.insert(studentGroupMapper.toEntity(dto)));
    }

    @PutMapping("/group/{id}")
    public StudentGroupDto update(@PathVariable int id, @Valid @RequestBody StudentGroupDto dto) {
        var entity = studentGroupMapper.toEntity(dto);
        entity.setId(id);
        return StudentGroupDto.toDto(studentGroupService.update(entity));
    }

    @DeleteMapping("/group/{id}")
    public void deleteById(@PathVariable int id) {
        studentGroupService.deleteById(id);
    }

    private ClassResponse toClassResponse(StudentGroup group) {
        int count = studentService.getByGroupId(group.getId()).size();
        return ClassResponse.builder()
                .id(group.getId())
                .className(group.getName())
                .studentsCount(count)
                .code(group.getCode())
                .courseNumber(group.getCourseNumber())
                .build();
    }
}
