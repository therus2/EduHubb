package com.eduhab.rest.dto;

import com.eduhab.domain.UserSettings;
import lombok.*;
import javax.validation.constraints.NotNull;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettingsDto {
    @NotNull
    private int userId;
    private Boolean notifyNewGrades;
    private Boolean notifyScheduleChanges;
    private Boolean notifyHomework;
    private Boolean notifyAnnouncements;
    private String theme;
    private String language;

    public static UserSettingsDto toDto(UserSettings settings) {
        return UserSettingsDto.builder()
                .userId(settings.getUserId())
                .notifyNewGrades(settings.getNotifyNewGrades())
                .notifyScheduleChanges(settings.getNotifyScheduleChanges())
                .notifyHomework(settings.getNotifyHomework())
                .notifyAnnouncements(settings.getNotifyAnnouncements())
                .theme(settings.getTheme())
                .language(settings.getLanguage())
                .build();
    }
}
