package com.eduhab.rest.controller;

import com.eduhab.domain.Grade;
import com.eduhab.domain.Student;
import com.eduhab.rest.dto.GradeDto;
import com.eduhab.rest.dto.response.GradeResponse;
import com.eduhab.rest.dto.response.JournalGradeResponse;
import com.eduhab.rest.dto.response.SubjectAverageResponse;
import com.eduhab.rest.mapper.GradeMapper;
import com.eduhab.service.GradeService;
import com.eduhab.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class GradeController {
    private final GradeService gradeService;
    private final GradeMapper gradeMapper;
    private final StudentService studentService;

    @GetMapping("/grade")
    public List<GradeResponse> getAll(
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer subjectId,
            @RequestParam(required = false) Integer teacherId,
            @RequestParam(required = false) Integer lessonId) {
        List<Grade> grades;
        if (lessonId != null) {
            grades = gradeService.getByLessonId(lessonId);
        } else if (studentId != null && subjectId != null) {
            grades = gradeService.getByStudentIdAndSubjectId(studentId, subjectId);
        } else if (studentId != null) {
            grades = gradeService.getByStudentId(studentId);
        } else if (teacherId != null) {
            grades = gradeService.getByTeacherId(teacherId);
        } else {
            grades = gradeService.getAll();
        }
        return grades.stream().map(this::toGradeResponse).collect(Collectors.toList());
    }

    @GetMapping("/grade/{id}")
    public GradeResponse getById(@PathVariable int id) {
        return toGradeResponse(gradeService.getById(id));
    }

    @PostMapping("/grade")
    public GradeResponse insert(@Valid @RequestBody GradeDto dto) {
        return toGradeResponse(gradeService.insert(gradeMapper.toEntity(dto)));
    }

    @PostMapping("/grade/batch")
    public List<GradeResponse> insertBatch(@Valid @RequestBody List<GradeDto> dtos) {
        return dtos.stream()
                .map(dto -> toGradeResponse(gradeService.insert(gradeMapper.toEntity(dto))))
                .collect(Collectors.toList());
    }

    @PutMapping("/grade/{id}")
    public GradeResponse update(@PathVariable int id, @Valid @RequestBody GradeDto dto) {
        var entity = gradeMapper.toEntity(dto);
        entity.setId(id);
        return toGradeResponse(gradeService.update(entity));
    }

    @DeleteMapping("/grade/{id}")
    public void deleteById(@PathVariable int id) {
        gradeService.deleteById(id);
    }

    @GetMapping("/grade/averages")
    public List<SubjectAverageResponse> getAverages(@RequestParam int studentId) {
        List<Grade> grades = gradeService.getByStudentId(studentId);
        Map<Integer, List<Grade>> bySubject = grades.stream()
                .collect(Collectors.groupingBy(g -> g.getSubject().getId()));

        List<SubjectAverageResponse> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Grade>> entry : bySubject.entrySet()) {
            List<Grade> subjectGrades = entry.getValue();
            double avg = subjectGrades.stream()
                    .mapToInt(Grade::getValue)
                    .average()
                    .orElse(0.0);
            Grade first = subjectGrades.get(0);
            int gradeCount = subjectGrades.size();
            int progress = (int) Math.round((avg - 2.0) / 3.0 * 100);
            String statusText = avg >= 4.5 ? "Отлично" : avg >= 3.5 ? "Хорошо" : avg >= 2.5 ? "Удовл." : "Неуд.";
            String teacherName = first.getTeacher().getUser().getLastName() + " "
                    + first.getTeacher().getUser().getFirstName().charAt(0) + ".";

            result.add(SubjectAverageResponse.builder()
                    .subjectId(first.getSubject().getId())
                    .subjectName(first.getSubject().getName())
                    .average(Math.round(avg * 100.0) / 100.0)
                    .progress(Math.min(progress, 100))
                    .statusText(statusText)
                    .teacherName(teacherName)
                    .gradeCount(gradeCount)
                    .build());
        }
        return result;
    }

    @GetMapping("/grade/journal")
    public List<JournalGradeResponse> getJournal(
            @RequestParam int lessonId,
            @RequestParam int groupId) {
        List<Grade> grades = gradeService.getByLessonId(lessonId);
        Map<Integer, List<Grade>> byStudent = grades.stream()
                .collect(Collectors.groupingBy(g -> g.getStudent().getId()));

        List<Student> students = studentService.getByGroupId(groupId);
        List<JournalGradeResponse> result = new ArrayList<>();

        for (Student student : students) {
            List<Grade> studentGrades = byStudent.getOrDefault(student.getId(), List.of());
            String name = student.getUser().getLastName() + " "
                    + student.getUser().getFirstName().charAt(0) + "."
                    + (student.getUser().getPatronymic() != null ? student.getUser().getPatronymic().charAt(0) + "." : "");
            double avg = studentGrades.stream().mapToInt(Grade::getValue).average().orElse(0.0);

            result.add(JournalGradeResponse.builder()
                    .studentId(student.getId())
                    .studentName(name)
                    .averageMark(String.format("%.2f", avg))
                    .marks(studentGrades.stream().map(Grade::getValue).collect(Collectors.toList()))
                    .gradeIds(studentGrades.stream().map(Grade::getId).collect(Collectors.toList()))
                    .build());
        }
        return result;
    }

    private GradeResponse toGradeResponse(Grade grade) {
        String teacherName = grade.getTeacher().getUser().getLastName() + " "
                + grade.getTeacher().getUser().getFirstName().charAt(0) + ".";
        return GradeResponse.builder()
                .gradeId(grade.getId())
                .value(grade.getValue())
                .gradeType(grade.getGradeType())
                .weight(grade.getWeight() != null ? grade.getWeight() : 1)
                .comment(grade.getComment())
                .teacherName(teacherName)
                .subjectName(grade.getSubject().getName())
                .createdAt(grade.getCreatedAt() != null ? grade.getCreatedAt().toString() : null)
                .lessonId(grade.getLesson() != null ? grade.getLesson().getId() : null)
                .studentId(grade.getStudent().getId())
                .subjectId(grade.getSubject().getId())
                .teacherId(grade.getTeacher().getId())
                .build();
    }
}
