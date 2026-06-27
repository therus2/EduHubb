package com.example.eduhub.domains.classes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Announcement {
    private UUID id;
    private String title;
    private String content;
    private Teacher author;
    private StudentGroup targetGroup;
    private AnnouncementPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;
    private boolean isPublished;
    private boolean isPinned;
    private Set<String> attachmentUrls;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Teacher getAuthor() { return author; }
    public void setAuthor(Teacher author) { this.author = author; }

    public StudentGroup getTargetGroup() { return targetGroup; }
    public void setTargetGroup(StudentGroup targetGroup) { this.targetGroup = targetGroup; }

    public AnnouncementPriority getPriority() { return priority; }
    public void setPriority(AnnouncementPriority priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }

    public boolean isPinned() { return isPinned; }
    public void setPinned(boolean pinned) { isPinned = pinned; }

    public Set<String> getAttachmentUrls() { return attachmentUrls; }
    public void setAttachmentUrls(Set<String> attachmentUrls) { this.attachmentUrls = attachmentUrls; }

    public Announcement() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.attachmentUrls = new HashSet<>();
        this.isPublished = false;
    }

    public Announcement(String title, String content, Teacher author, StudentGroup targetGroup) {
        this();
        this.title = title;
        this.content = content;
        this.author = author;
        this.targetGroup = targetGroup;
        this.priority = AnnouncementPriority.NORMAL;
    }

    public void publish() {
        this.isPublished = true;
        this.publishedAt = LocalDateTime.now();
    }

    public void unpublish() {
        this.isPublished = false;
    }

    public void addAttachment(String url) {
        attachmentUrls.add(url);
    }

    public void removeAttachment(String url) {
        attachmentUrls.remove(url);
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return isPublished && !isExpired();
    }

    public enum AnnouncementPriority {
        LOW,
        NORMAL,
        IMPORTANT,
        URGENT
    }
}
