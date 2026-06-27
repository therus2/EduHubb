package com.eduhab.rest.controller;

import com.eduhab.domain.Attendance;
import com.eduhab.rest.dto.AttendanceDto;
import com.eduhab.rest.dto.response.AttendanceResponse;
import com.eduhab.rest.mapper.AttendanceMapper;
import com.eduhab.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final AttendanceMapper attendanceMapper;

    @GetMapping("/attendance")
    public List<AttendanceResponse> getAll(
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) Integer lessonId,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        List<Attendance> records;
        if (studentId != null && dateFrom != null && dateTo != null) {
            records = attendanceService.getByStudentIdAndDateBetween(studentId, LocalDate.parse(dateFrom), LocalDate.parse(dateTo));
        } else if (lessonId != null) {
            records = attendanceService.getByLessonId(lessonId);
        } else if (studentId != null) {
            records = attendanceService.getByStudentId(studentId);
        } else {
            records = attendanceService.getAll();
        }
        return records.stream().map(this::toAttendanceResponse).collect(Collectors.toList());
    }

    @GetMapping("/attendance/{id}")
    public AttendanceResponse getById(@PathVariable int id) {
        return toAttendanceResponse(attendanceService.getById(id));
    }

    @PostMapping("/attendance")
    public AttendanceDto insert(@Valid @RequestBody AttendanceDto dto) {
        return AttendanceDto.toDto(attendanceService.insert(attendanceMapper.toEntity(dto)));
    }

    @PutMapping("/attendance/{id}")
    public AttendanceDto update(@PathVariable int id, @Valid @RequestBody AttendanceDto dto) {
        var entity = attendanceMapper.toEntity(dto);
        entity.setId(id);
        return AttendanceDto.toDto(attendanceService.update(entity));
    }

    @DeleteMapping("/attendance/{id}")
    public void deleteById(@PathVariable int id) {
        attendanceService.deleteById(id);
    }

    private AttendanceResponse toAttendanceResponse(Attendance attendance) {
        String statusText = attendance.getStatus();
        if ("PRESENT".equals(attendance.getStatus())) statusText = "Присутствует";
        else if ("ABSENT".equals(attendance.getStatus())) statusText = "Отсутствует";
        else if ("LATE".equals(attendance.getStatus())) statusText = "Опоздал";
        else if ("EXCUSED_ABSENT".equals(attendance.getStatus())) statusText = "Пропуск (ув.)";
        else if ("EARLY_LEAVE".equals(attendance.getStatus())) statusText = "Ушёл раньше";
        String className = attendance.getLesson() != null
                ? attendance.getLesson().getSubject().getName() : null;
        String lessonTopic = attendance.getLesson() != null
                ? attendance.getLesson().getLessonTopic() : null;
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .className(className)
                .date(attendance.getDate().toString())
                .status(attendance.getStatus())
                .statusText(statusText)
                .comment(attendance.getComment())
                .lessonTopic(lessonTopic)
                .studentId(attendance.getStudent().getId())
                .lessonId(attendance.getLesson() != null ? attendance.getLesson().getId() : null)
                .build();
    }
}
