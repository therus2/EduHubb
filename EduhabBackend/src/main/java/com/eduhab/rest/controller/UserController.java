package com.eduhab.rest.controller;

import com.eduhab.domain.User;
import com.eduhab.rest.dto.UserDto;
import com.eduhab.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user")
    public List<UserDto> getAll() {
        return userService.getAll().stream().map(UserDto::toDto).collect(Collectors.toList());
    }

    @GetMapping("/user/{id}")
    public UserDto getById(@PathVariable int id) {
        return UserDto.toDto(userService.getById(id));
    }

    @PostMapping("/user")
    public UserDto insert(@Valid @RequestBody UserDto dto) {
        User user = UserDto.toDomainObject(dto);
        return UserDto.toDto(userService.insert(user));
    }

    @PutMapping("/user/{id}")
    public UserDto update(@PathVariable int id, @Valid @RequestBody UserDto dto) {
        User user = UserDto.toDomainObject(dto);
        user.setId(id);
        return UserDto.toDto(userService.update(user));
    }

    @DeleteMapping("/user/{id}")
    public void deleteById(@PathVariable int id) {
        userService.deleteById(id);
    }
}
