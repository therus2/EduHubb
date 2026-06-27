package com.eduhab.rest.dto;

import com.eduhab.domain.AnnouncementRead;
import lombok.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementReadDto {
    private int id;
    @NotNull
    private int announcementId;
    @NotNull
    private int studentId;
    private LocalDateTime readAt;

    public static AnnouncementReadDto toDto(AnnouncementRead ar) {
        return AnnouncementReadDto.builder()
                .id(ar.getId())
                .announcementId(ar.getAnnouncement().getId())
                .studentId(ar.getStudent().getId())
                .readAt(ar.getReadAt())
                .build();
    }
}
