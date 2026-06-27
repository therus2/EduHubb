package com.eduhab.rest.controller;

import com.eduhab.rest.dto.SubjectDto;
import com.eduhab.rest.mapper.SubjectMapper;
import com.eduhab.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;
    private final SubjectMapper subjectMapper;

    @GetMapping("/subject")
    public List<SubjectDto> getAll() {
        return subjectService.getAll().stream().map(SubjectDto::toDto).collect(Collectors.toList());
    }

    @GetMapping("/subject/{id}")
    public SubjectDto getById(@PathVariable int id) {
        return SubjectDto.toDto(subjectService.getById(id));
    }

    @PostMapping("/subject")
    public SubjectDto insert(@Valid @RequestBody SubjectDto dto) {
        return SubjectDto.toDto(subjectService.insert(subjectMapper.toEntity(dto)));
    }

    @PutMapping("/subject/{id}")
    public SubjectDto update(@PathVariable int id, @Valid @RequestBody SubjectDto dto) {
        var entity = subjectMapper.toEntity(dto);
        entity.setId(id);
        return SubjectDto.toDto(subjectService.update(entity));
    }

    @DeleteMapping("/subject/{id}")
    public void deleteById(@PathVariable int id) {
        subjectService.deleteById(id);
    }
}
