package com.example.eduhub.schedule.models;


public abstract class ScheduleItem {
    public static final int TYPE_LESSON = 0;
    public static final int TYPE_BREAK = 1;

    private boolean isExpanded = false;

    public abstract int getType();

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
