package com.eduhab.rest.dto;

import com.eduhab.domain.Announcement;
import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementDto {
    private int id;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private int authorId;
    private Integer targetGroupId;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;
    private Boolean isPublished;
    private Boolean isPinned;
    private String attachments;

    public static AnnouncementDto toDto(Announcement announcement) {
        return AnnouncementDto.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .authorId(announcement.getAuthor().getId())
                .targetGroupId(announcement.getTargetGroup() != null ? announcement.getTargetGroup().getId() : null)
                .priority(announcement.getPriority())
                .createdAt(announcement.getCreatedAt())
                .publishedAt(announcement.getPublishedAt())
                .expiresAt(announcement.getExpiresAt())
                .isPublished(announcement.getIsPublished())
                .isPinned(announcement.getIsPinned())
                .attachments(announcement.getAttachments())
                .build();
    }
}
