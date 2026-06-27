package com.eduhab.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private int id;
    private String icon;
    private String title;
    private String subtitle;
    private String details;
    private String time;
    private boolean isRead;
    private int userId;
    private String notificationType;
}
