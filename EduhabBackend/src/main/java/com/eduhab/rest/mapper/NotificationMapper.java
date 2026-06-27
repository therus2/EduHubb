package com.eduhab.rest.mapper;

import com.eduhab.domain.Notification;
import com.eduhab.rest.dto.NotificationDto;
import com.eduhab.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMapper {
    private final UserService userService;

    public Notification toEntity(NotificationDto dto) {
        Notification notification = new Notification();
        notification.setUser(userService.getById(dto.getUserId()));
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setNotificationType(dto.getNotificationType());
        notification.setIsRead(dto.getIsRead());
        notification.setCreatedAt(dto.getCreatedAt());
        notification.setReadAt(dto.getReadAt());
        notification.setActionUrl(dto.getActionUrl());
        return notification;
    }
}
