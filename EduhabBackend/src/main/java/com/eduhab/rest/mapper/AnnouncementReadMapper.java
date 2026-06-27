package com.eduhab.rest.mapper;

import com.eduhab.domain.AnnouncementRead;
import com.eduhab.rest.dto.AnnouncementReadDto;
import com.eduhab.service.AnnouncementService;
import com.eduhab.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnnouncementReadMapper {
    private final AnnouncementService announcementService;
    private final StudentService studentService;

    public AnnouncementRead toEntity(AnnouncementReadDto dto) {
        AnnouncementRead ar = new AnnouncementRead();
        ar.setAnnouncement(announcementService.getById(dto.getAnnouncementId()));
        ar.setStudent(studentService.getById(dto.getStudentId()));
        ar.setReadAt(dto.getReadAt());
        return ar;
    }
}
