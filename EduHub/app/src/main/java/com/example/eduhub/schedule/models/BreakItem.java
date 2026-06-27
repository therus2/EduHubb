package com.example.eduhub.schedule.models;


public class BreakItem extends ScheduleItem {
    private String text;

    public BreakItem(String text) {
        this.text = text;
    }

    public String getText() { return text; }

    @Override
    public int getType() {
        return TYPE_BREAK;
    }
}
