package com.example.eduhub.teacher.models;

public class GradeDetailItem {
    private String gradeId;
    private int value;
    private String subjectName;
    private String date;
    private String type;
    private String comment;

    public GradeDetailItem(String gradeId, int value, String subjectName, String date, String type, String comment) {
        this.gradeId = gradeId;
        this.value = value;
        this.subjectName = subjectName;
        this.date = date;
        this.type = type;
        this.comment = comment;
    }

    public String getGradeId() { return gradeId; }
    public int getValue() { return value; }
    public String getSubjectName() { return subjectName; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public String getComment() { return comment; }
}
