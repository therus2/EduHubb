package com.eduhab.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementResponse {
    private int id;
    private String tag;
    private String title;
    private String description;
    private String fullText;
    private String time;
    private boolean isUnread;
    private boolean isAcknowledged;
    private int authorId;
    private Integer targetGroupId;
    private String priority;
    private Boolean isPublished;
    private Boolean isPinned;
}
