package com.eduhab.rest.dto;

import com.eduhab.domain.Lesson;
import lombok.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDto {
    private int id;
    @NotNull
    private int groupId;
    @NotNull
    private int subjectId;
    @NotNull
    private int teacherId;
    @NotNull
    private Integer dayOfWeek;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    private String classroom;
    private String lessonType;
    private Integer weekNumber;
    private Boolean isAlternatingWeek;
    private String lessonTopic;

    public static LessonDto toDto(Lesson lesson) {
        return LessonDto.builder()
                .id(lesson.getId())
                .groupId(lesson.getGroup().getId())
                .subjectId(lesson.getSubject().getId())
                .teacherId(lesson.getTeacher().getId())
                .dayOfWeek(lesson.getDayOfWeek())
                .startTime(lesson.getStartTime())
                .endTime(lesson.getEndTime())
                .classroom(lesson.getClassroom())
                .lessonType(lesson.getLessonType())
                .weekNumber(lesson.getWeekNumber())
                .isAlternatingWeek(lesson.getIsAlternatingWeek())
                .lessonTopic(lesson.getLessonTopic())
                .build();
    }
}
