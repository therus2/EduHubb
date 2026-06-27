package com.eduhab.rest.controller;

import com.eduhab.domain.Notification;
import com.eduhab.rest.dto.NotificationDto;
import com.eduhab.rest.dto.response.NotificationResponse;
import com.eduhab.rest.mapper.NotificationMapper;
import com.eduhab.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping("/notification")
    public List<NotificationResponse> getAll(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Boolean isRead) {
        List<Notification> notifications;
        if (userId != null) {
            notifications = notificationService.getByUserIdAndIsRead(userId, isRead);
        } else {
            notifications = notificationService.getAll();
        }
        return notifications.stream().map(this::toNotificationResponse).collect(Collectors.toList());
    }

    @GetMapping("/notification/{id}")
    public NotificationResponse getById(@PathVariable int id) {
        return toNotificationResponse(notificationService.getById(id));
    }

    @PostMapping("/notification")
    public NotificationDto insert(@Valid @RequestBody NotificationDto dto) {
        return NotificationDto.toDto(notificationService.insert(notificationMapper.toEntity(dto)));
    }

    @PutMapping("/notification/{id}")
    public NotificationDto update(@PathVariable int id, @Valid @RequestBody NotificationDto dto) {
        var entity = notificationMapper.toEntity(dto);
        entity.setId(id);
        return NotificationDto.toDto(notificationService.update(entity));
    }

    @DeleteMapping("/notification/{id}")
    public void deleteById(@PathVariable int id) {
        notificationService.deleteById(id);
    }

    private NotificationResponse toNotificationResponse(Notification notification) {
        String type = notification.getNotificationType();
        String icon = "default";
        if ("NEW_GRADE".equals(type)) icon = "grade";
        else if ("SCHEDULE_CHANGE".equals(type)) icon = "schedule";
        else if ("HOMEWORK".equals(type)) icon = "homework";
        else if ("ANNOUNCEMENT".equals(type)) icon = "announcement";
        String time = notification.getCreatedAt() != null
                ? notification.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                : null;
        return NotificationResponse.builder()
                .id(notification.getId())
                .icon(icon)
                .title(notification.getTitle())
                .subtitle(notification.getNotificationType())
                .details(notification.getMessage())
                .time(time)
                .isRead(notification.getIsRead() != null && notification.getIsRead())
                .userId(notification.getUser().getId())
                .notificationType(notification.getNotificationType())
                .build();
    }
}
