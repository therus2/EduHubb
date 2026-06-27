package com.example.eduhub.teacher.models;

public class AttendanceDetailItem {
    private String attId;
    private String date;
    private String status;
    private String comment;
    private String lessonTopic;
    private String subjectName;

    public AttendanceDetailItem(String attId, String date, String status, String comment, String lessonTopic, String subjectName) {
        this.attId = attId;
        this.date = date;
        this.status = status;
        this.comment = comment;
        this.lessonTopic = lessonTopic;
        this.subjectName = subjectName;
    }

    public String getAttId() { return attId; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
    public String getComment() { return comment; }
    public String getLessonTopic() { return lessonTopic; }
    public String getSubjectName() { return subjectName; }
}
