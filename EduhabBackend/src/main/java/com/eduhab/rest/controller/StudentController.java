package com.eduhab.rest.controller;

import com.eduhab.domain.Student;
import com.eduhab.rest.dto.StudentDto;
import com.eduhab.rest.dto.response.StudentInfoResponse;
import com.eduhab.rest.mapper.StudentMapper;
import com.eduhab.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    private final StudentMapper studentMapper;

    @GetMapping("/student")
    public List<StudentDto> getAll(@RequestParam(required = false) Integer groupId) {
        List<Student> students;
        if (groupId != null) {
            students = studentService.getByGroupId(groupId);
        } else {
            students = studentService.getAll();
        }
        return students.stream().map(StudentDto::toDto).collect(Collectors.toList());
    }

    @GetMapping("/student/{id}")
    public StudentDto getById(@PathVariable int id) {
        return StudentDto.toDto(studentService.getById(id));
    }

    @PostMapping("/student")
    public StudentDto insert(@Valid @RequestBody StudentDto dto) {
        return StudentDto.toDto(studentService.insert(studentMapper.toEntity(dto)));
    }

    @PutMapping("/student/{id}")
    public StudentDto update(@PathVariable int id, @Valid @RequestBody StudentDto dto) {
        var entity = studentMapper.toEntity(dto);
        entity.setId(id);
        return StudentDto.toDto(studentService.update(entity));
    }

    @DeleteMapping("/student/{id}")
    public void deleteById(@PathVariable int id) {
        studentService.deleteById(id);
    }

    @GetMapping("/student/info/{id}")
    public StudentInfoResponse getInfo(@PathVariable int id) {
        Student student = studentService.getById(id);
        String name = student.getUser().getLastName() + " "
                + student.getUser().getFirstName() + " "
                + (student.getUser().getPatronymic() != null ? student.getUser().getPatronymic() : "");
        return StudentInfoResponse.builder()
                .studentId(student.getId())
                .studentName(name)
                .phoneNumber(student.getUser().getPhoneNumber())
                .groupCode(student.getGroup() != null ? student.getGroup().getCode() : null)
                .groupId(student.getGroup() != null ? student.getGroup().getId() : null)
                .build();
    }
}
