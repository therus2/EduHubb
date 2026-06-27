package com.eduhab.rest.controller;

import com.eduhab.rest.dto.AnnouncementReadDto;
import com.eduhab.rest.mapper.AnnouncementReadMapper;
import com.eduhab.service.AnnouncementReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AnnouncementReadController {
    private final AnnouncementReadService announcementReadService;
    private final AnnouncementReadMapper announcementReadMapper;

    @GetMapping("/announcement-read")
    public List<AnnouncementReadDto> getAll() {
        return announcementReadService.getAll().stream().map(AnnouncementReadDto::toDto).collect(Collectors.toList());
    }

    @GetMapping("/announcement-read/{id}")
    public AnnouncementReadDto getById(@PathVariable int id) {
        return AnnouncementReadDto.toDto(announcementReadService.getById(id));
    }

    @PostMapping("/announcement-read")
    public AnnouncementReadDto insert(@Valid @RequestBody AnnouncementReadDto dto) {
        return AnnouncementReadDto.toDto(announcementReadService.insert(announcementReadMapper.toEntity(dto)));
    }

    @PutMapping("/announcement-read/{id}")
    public AnnouncementReadDto update(@PathVariable int id, @Valid @RequestBody AnnouncementReadDto dto) {
        var entity = announcementReadMapper.toEntity(dto);
        entity.setId(id);
        return AnnouncementReadDto.toDto(announcementReadService.update(entity));
    }

    @DeleteMapping("/announcement-read/{id}")
    public void deleteById(@PathVariable int id) {
        announcementReadService.deleteById(id);
    }
}
