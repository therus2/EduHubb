package com.example.eduhub.school.announcements.models;

public class AnnouncementItem {
    private String id;
    private String tag;
    private String title;
    private String description;
    private String fullText;
    private String time;
    private boolean isUnread;
    private boolean isAcknowledged;
    private boolean isExpanded = false;

    public AnnouncementItem(String id, String tag, String title, String description, String fullText, String time, boolean isUnread, boolean isAcknowledged) {
        this.id = id;
        this.tag = tag;
        this.title = title;
        this.description = description;
        this.fullText = fullText;
        this.time = time;
        this.isUnread = isUnread;
        this.isAcknowledged = isAcknowledged;
    }

    public String getId() { return id; }
    public String getTag() { return tag; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getFullText() { return fullText; }
    public String getTime() { return time; }
    public boolean isUnread() { return isUnread; }
    public boolean isAcknowledged() { return isAcknowledged; }
    public boolean isExpanded() { return isExpanded; }

    public void setAcknowledged(boolean acknowledged) { isAcknowledged = acknowledged; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }
    public void setUnread(boolean unread) { isUnread = unread; }
}
