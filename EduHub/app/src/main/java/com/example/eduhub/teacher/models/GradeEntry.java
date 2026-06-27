package com.example.eduhub.teacher.models;

public class GradeEntry {
    private String studentId;
    private String gradeValue;
    private String gradeType;
    private String gradeDate;
    private String comment;

    public GradeEntry(String studentId, String gradeValue, String gradeType, String gradeDate, String comment) {
        this.studentId = studentId;
        this.gradeValue = gradeValue;
        this.gradeType = gradeType;
        this.gradeDate = gradeDate;
        this.comment = comment;
    }

    public String getStudentId() { return studentId; }
    public String getGradeValue() { return gradeValue; }
    public void setGradeValue(String gradeValue) { this.gradeValue = gradeValue; }
    public String getGradeType() { return gradeType; }
    public void setGradeType(String gradeType) { this.gradeType = gradeType; }
    public String getGradeDate() { return gradeDate; }
    public void setGradeDate(String gradeDate) { this.gradeDate = gradeDate; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
