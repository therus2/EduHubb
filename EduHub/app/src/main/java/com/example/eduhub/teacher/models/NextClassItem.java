package com.example.eduhub.teacher.models;


public class NextClassItem {
    private String classSubject;
    private String timeRoom;
    private String statusBadge;
    private String lessonId;
    private String date;

    public NextClassItem(String classSubject, String timeRoom, String statusBadge) {
        this(classSubject, timeRoom, statusBadge, null, null);
    }

    public NextClassItem(String classSubject, String timeRoom, String statusBadge,
                         String lessonId, String date) {
        this.classSubject = classSubject;
        this.timeRoom = timeRoom;
        this.statusBadge = statusBadge;
        this.lessonId = lessonId;
        this.date = date;
    }

    public String getClassSubject() { return classSubject; }
    public String getTimeRoom() { return timeRoom; }
    public String getStatusBadge() { return statusBadge; }
    public String getLessonId() { return lessonId; }
    public String getDate() { return date; }
}
