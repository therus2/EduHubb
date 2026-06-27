package com.eduhab.rest.controller;

import com.eduhab.domain.Announcement;
import com.eduhab.rest.dto.AnnouncementDto;
import com.eduhab.rest.dto.response.AnnouncementResponse;
import com.eduhab.rest.mapper.AnnouncementMapper;
import com.eduhab.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;
    private final AnnouncementMapper announcementMapper;

    @GetMapping("/announcement")
    public List<AnnouncementResponse> getAll(
            @RequestParam(required = false) Integer targetGroupId,
            @RequestParam(required = false) Integer authorId) {
        List<Announcement> announcements;
        if (targetGroupId != null) {
            announcements = announcementService.findByTargetGroupId(targetGroupId);
        } else if (authorId != null) {
            announcements = announcementService.findByAuthorId(authorId);
        } else {
            announcements = announcementService.getPublished();
        }
        return announcements.stream().map(this::toAnnouncementResponse).collect(Collectors.toList());
    }

    @GetMapping("/announcement/{id}")
    public AnnouncementDto getById(@PathVariable int id) {
        return AnnouncementDto.toDto(announcementService.getById(id));
    }

    @PostMapping("/announcement")
    public AnnouncementDto insert(@Valid @RequestBody AnnouncementDto dto) {
        return AnnouncementDto.toDto(announcementService.insert(announcementMapper.toEntity(dto)));
    }

    @PutMapping("/announcement/{id}")
    public AnnouncementDto update(@PathVariable int id, @Valid @RequestBody AnnouncementDto dto) {
        var entity = announcementMapper.toEntity(dto);
        entity.setId(id);
        return AnnouncementDto.toDto(announcementService.update(entity));
    }

    @DeleteMapping("/announcement/{id}")
    public void deleteById(@PathVariable int id) {
        announcementService.deleteById(id);
    }

    private AnnouncementResponse toAnnouncementResponse(Announcement announcement) {
        String tag = announcement.getPriority() != null ? announcement.getPriority() : "common";
        String desc = announcement.getContent() != null && announcement.getContent().length() > 100
                ? announcement.getContent().substring(0, 100) + "..."
                : announcement.getContent();
        String time = announcement.getPublishedAt() != null ? announcement.getPublishedAt().toString() : null;

        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .tag(tag)
                .title(announcement.getTitle())
                .description(desc)
                .fullText(announcement.getContent())
                .time(time)
                .isUnread(true)
                .isAcknowledged(false)
                .authorId(announcement.getAuthor().getId())
                .targetGroupId(announcement.getTargetGroup() != null ? announcement.getTargetGroup().getId() : null)
                .priority(announcement.getPriority())
                .isPublished(announcement.getIsPublished())
                .isPinned(announcement.getIsPinned())
                .build();
    }
}
