package com.eduhab.rest.dto;

import com.eduhab.domain.ScheduleBreak;
import lombok.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleBreakDto {
    private int id;
    private Integer groupId;
    private Integer dayOfWeek;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    private String label;

    public static ScheduleBreakDto toDto(ScheduleBreak sb) {
        return ScheduleBreakDto.builder()
                .id(sb.getId())
                .groupId(sb.getGroup() != null ? sb.getGroup().getId() : null)
                .dayOfWeek(sb.getDayOfWeek())
                .startTime(sb.getStartTime())
                .endTime(sb.getEndTime())
                .label(sb.getLabel())
                .build();
    }
}
