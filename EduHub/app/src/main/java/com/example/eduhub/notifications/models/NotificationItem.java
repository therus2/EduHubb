package com.example.eduhub.notifications.models;

public class NotificationItem {
    private String id;
    private String icon;
    private String title;
    private String subtitle;
    private String details;
    private String time;
    private boolean isRead;
    private boolean isExpanded = false;

    public NotificationItem(String id, String icon, String title, String subtitle, String details, String time, boolean isRead) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.subtitle = subtitle;
        this.details = details;
        this.time = time;
        this.isRead = isRead;
    }

    public String getId() { return id; }
    public String getIcon() { return icon; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getDetails() { return details; }
    public String getTime() { return time; }
    public boolean isRead() { return isRead; }
    public boolean isExpanded() { return isExpanded; }

    public void setExpanded(boolean expanded) { isExpanded = expanded; }
}
