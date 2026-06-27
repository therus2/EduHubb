package com.eduhab.rest.mapper;

import com.eduhab.domain.ScheduleBreak;
import com.eduhab.rest.dto.ScheduleBreakDto;
import com.eduhab.service.StudentGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleBreakMapper {
    private final StudentGroupService studentGroupService;

    public ScheduleBreak toEntity(ScheduleBreakDto dto) {
        ScheduleBreak sb = new ScheduleBreak();
        sb.setGroup(dto.getGroupId() != null ? studentGroupService.getById(dto.getGroupId()) : null);
        sb.setDayOfWeek(dto.getDayOfWeek());
        sb.setStartTime(dto.getStartTime());
        sb.setEndTime(dto.getEndTime());
        sb.setLabel(dto.getLabel());
        return sb;
    }
}
