package com.eduhab.rest.mapper;

import com.eduhab.domain.Announcement;
import com.eduhab.rest.dto.AnnouncementDto;
import com.eduhab.service.StudentGroupService;
import com.eduhab.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnnouncementMapper {
    private final TeacherService teacherService;
    private final StudentGroupService studentGroupService;

    public Announcement toEntity(AnnouncementDto dto) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setAuthor(teacherService.getById(dto.getAuthorId()));
        announcement.setTargetGroup(dto.getTargetGroupId() != null ? studentGroupService.getById(dto.getTargetGroupId()) : null);
        announcement.setPriority(dto.getPriority());
        announcement.setCreatedAt(dto.getCreatedAt());
        announcement.setPublishedAt(dto.getPublishedAt());
        announcement.setExpiresAt(dto.getExpiresAt());
        announcement.setIsPublished(dto.getIsPublished());
        announcement.setIsPinned(dto.getIsPinned());
        announcement.setAttachments(dto.getAttachments());
        return announcement;
    }
}
