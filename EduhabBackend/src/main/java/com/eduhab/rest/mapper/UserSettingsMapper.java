package com.eduhab.rest.mapper;

import com.eduhab.domain.UserSettings;
import com.eduhab.rest.dto.UserSettingsDto;
import com.eduhab.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSettingsMapper {
    private final UserService userService;

    public UserSettings toEntity(UserSettingsDto dto) {
        UserSettings us = new UserSettings();
        us.setUserId(dto.getUserId());
        us.setUser(userService.getById(dto.getUserId()));
        us.setNotifyNewGrades(dto.getNotifyNewGrades());
        us.setNotifyScheduleChanges(dto.getNotifyScheduleChanges());
        us.setNotifyHomework(dto.getNotifyHomework());
        us.setNotifyAnnouncements(dto.getNotifyAnnouncements());
        us.setTheme(dto.getTheme());
        us.setLanguage(dto.getLanguage());
        return us;
    }
}
