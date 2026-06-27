package com.example.eduhub.school.attendance.models;

public class AttendanceItem {
    private String className;
    private String time;
    private String statusText;
    private int statusType; 
    private String details;
    private boolean isExpanded = false;

    public AttendanceItem(String className, String time, String statusText, int statusType, String details) {
        this.className = className;
        this.time = time;
        this.statusText = statusText;
        this.statusType = statusType;
        this.details = details;
    }

    public String getClassName() { return className; }
    public String getTime() { return time; }
    public String getStatusText() { return statusText; }
    public int getStatusType() { return statusType; }
    public String getDetails() { return details; }
    public boolean isExpanded() { return isExpanded; }

    public void setExpanded(boolean expanded) { isExpanded = expanded; }

    public boolean hasExpandableContent() {
        return details != null && !details.trim().isEmpty();
    }
}
