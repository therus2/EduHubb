package com.example.eduhub.marks.models;


public class MarkDateItem {
    private String dateHeader;
    private String subjectName;
    private String description;
    private int grade;
    private String teacher;
    private String comment;
    private boolean isExpanded = false;

    public MarkDateItem(String dateHeader, String subjectName, String description, int grade, String teacher, String comment) {
        this.dateHeader = dateHeader;
        this.subjectName = subjectName;
        this.description = description;
        this.grade = grade;
        this.teacher = teacher;
        this.comment = comment;
    }

    public String getDateHeader() { return dateHeader; }
    public String getSubjectName() { return subjectName; }
    public String getDescription() { return description; }
    public int getGrade() { return grade; }
    public String getTeacher() { return teacher; }
    public String getComment() { return comment; }
    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }
}
