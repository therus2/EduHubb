package com.example.eduhub.schedule.models;


public class LessonItem extends ScheduleItem {
    private String lessonId;
    private String info;
    private String cabinet;
    private String title;
    private String teacherName;
    private String homework;
    private boolean expanded;

    public LessonItem(String lessonId, String info, String cabinet, String title, String teacherName, String homework) {
        this.lessonId = lessonId;
        this.info = info;
        this.cabinet = cabinet;
        this.title = title;
        this.teacherName = teacherName;
        this.homework = homework;
    }

    public String getLessonId() { return lessonId; }
    public String getInfo() { return info; }
    public String getCabinet() { return cabinet; }
    public String getTitle() { return title; }
    public String getTeacherName() { return teacherName; }
    public String getHomework() { return homework; }
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }

    @Override
    public int getType() {
        return TYPE_LESSON;
    }
}
