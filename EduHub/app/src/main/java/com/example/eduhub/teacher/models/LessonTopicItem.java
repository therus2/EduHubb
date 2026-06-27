package com.example.eduhub.teacher.models;

public class LessonTopicItem {
    private String lessonId;
    private String topic;
    private String groupCode;
    private int dayOfWeek;
    private String startTime;
    private String endTime;
    private String classroom;
    private String lessonType;

    public LessonTopicItem(String lessonId, String topic, String groupCode, int dayOfWeek,
                           String startTime, String endTime, String classroom, String lessonType) {
        this.lessonId = lessonId;
        this.topic = topic;
        this.groupCode = groupCode;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroom = classroom;
        this.lessonType = lessonType;
    }

    public String getLessonId() { return lessonId; }
    public String getTopic() { return topic; }
    public String getGroupCode() { return groupCode; }
    public int getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getClassroom() { return classroom; }
    public String getLessonType() { return lessonType; }

    public String getDayName() {
        String[] days = {"", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
        return dayOfWeek >= 1 && dayOfWeek <= 7 ? days[dayOfWeek] : "";
    }
}
