package com.eduhab.service;

import com.eduhab.domain.UserSettings;
import java.util.List;

public interface UserSettingsService {
    UserSettings insert(UserSettings userSettings);
    UserSettings getById(int id);
    List<UserSettings> getAll();
    UserSettings update(UserSettings userSettings);
    void deleteById(int id);
}
