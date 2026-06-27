package com.eduhab.rest.controller;

import com.eduhab.rest.dto.ScheduleBreakDto;
import com.eduhab.rest.mapper.ScheduleBreakMapper;
import com.eduhab.service.ScheduleBreakService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ScheduleBreakController {
    private final ScheduleBreakService scheduleBreakService;
    private final ScheduleBreakMapper scheduleBreakMapper;

    @GetMapping("/schedule-break")
    public List<ScheduleBreakDto> getAll() {
        return scheduleBreakService.getAll().stream().map(ScheduleBreakDto::toDto).collect(Collectors.toList());
    }

    @GetMapping("/schedule-break/{id}")
    public ScheduleBreakDto getById(@PathVariable int id) {
        return ScheduleBreakDto.toDto(scheduleBreakService.getById(id));
    }

    @PostMapping("/schedule-break")
    public ScheduleBreakDto insert(@Valid @RequestBody ScheduleBreakDto dto) {
        return ScheduleBreakDto.toDto(scheduleBreakService.insert(scheduleBreakMapper.toEntity(dto)));
    }

    @PutMapping("/schedule-break/{id}")
    public ScheduleBreakDto update(@PathVariable int id, @Valid @RequestBody ScheduleBreakDto dto) {
        var entity = scheduleBreakMapper.toEntity(dto);
        entity.setId(id);
        return ScheduleBreakDto.toDto(scheduleBreakService.update(entity));
    }

    @DeleteMapping("/schedule-break/{id}")
    public void deleteById(@PathVariable int id) {
        scheduleBreakService.deleteById(id);
    }
}
