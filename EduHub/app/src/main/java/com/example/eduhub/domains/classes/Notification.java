package com.example.eduhub.domains.classes;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {
    private UUID id;
    private User recipient;
    private String title;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private String actionUrl;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }

    public Notification() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public Notification(User recipient, String title, String message, NotificationType type) {
        this();
        this.recipient = recipient;
        this.title = title;
        this.message = message;
        this.type = type;
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public void markAsUnread() {
        this.isRead = false;
        this.readAt = null;
    }

    public boolean isNew() {
        return !isRead && createdAt.isAfter(LocalDateTime.now().minusDays(7));
    }

    public enum NotificationType {
        NEW_GRADE,
        SCHEDULE_CHANGE,
        NEW_HOMEWORK,
        HOMEWORK_DEADLINE,
        ANNOUNCEMENT,
        ATTENDANCE,
        SYSTEM,
        MESSAGE
    }
}