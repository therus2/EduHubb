package com.eduhab.service;

import com.eduhab.domain.UserSettings;
import com.eduhab.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserSettingsServiceImpl implements UserSettingsService {
    private final UserSettingsRepository userSettingsRepository;

    @Override
    public List<UserSettings> getAll() { return userSettingsRepository.findAll(); }

    @Override
    public UserSettings insert(UserSettings userSettings) { return userSettingsRepository.save(userSettings); }

    @Override
    public UserSettings getById(int id) { return userSettingsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("UserSettings not found: " + id)); }

    @Override
    public UserSettings update(UserSettings userSettings) { return userSettingsRepository.save(userSettings); }

    @Override
    public void deleteById(int id) { userSettingsRepository.deleteById(id); }
}
