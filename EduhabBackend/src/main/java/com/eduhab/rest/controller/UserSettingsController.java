package com.eduhab.rest.controller;

import com.eduhab.rest.dto.UserSettingsDto;
import com.eduhab.rest.mapper.UserSettingsMapper;
import com.eduhab.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserSettingsController {
    private final UserSettingsService userSettingsService;
    private final UserSettingsMapper userSettingsMapper;

    @GetMapping("/user-settings")
    public List<UserSettingsDto> getAll() {
        return userSettingsService.getAll().stream().map(UserSettingsDto::toDto).collect(Collectors.toList());
    }

    @GetMapping("/user-settings/{userId}")
    public UserSettingsDto getById(@PathVariable int userId) {
        return UserSettingsDto.toDto(userSettingsService.getById(userId));
    }

    @PostMapping("/user-settings")
    public UserSettingsDto insert(@Valid @RequestBody UserSettingsDto dto) {
        return UserSettingsDto.toDto(userSettingsService.insert(userSettingsMapper.toEntity(dto)));
    }

    @PutMapping("/user-settings/{userId}")
    public UserSettingsDto update(@PathVariable int userId, @Valid @RequestBody UserSettingsDto dto) {
        var entity = userSettingsMapper.toEntity(dto);
        entity.setUserId(userId);
        return UserSettingsDto.toDto(userSettingsService.update(entity));
    }

    @DeleteMapping("/user-settings/{userId}")
    public void deleteById(@PathVariable int userId) {
        userSettingsService.deleteById(userId);
    }
}
