package com.example.eduhub.teacher.models;


public class TaskToCheckItem {
    private String id;
    private String title;
    private String subtitle;
    private int progress;
    private String groupId;

    public TaskToCheckItem(String id, String title, String subtitle, int progress) {
        this(id, title, subtitle, progress, null);
    }

    public TaskToCheckItem(String id, String title, String subtitle, int progress, String groupId) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.progress = progress;
        this.groupId = groupId;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getProgress() { return progress; }
    public String getGroupId() { return groupId; }
}
