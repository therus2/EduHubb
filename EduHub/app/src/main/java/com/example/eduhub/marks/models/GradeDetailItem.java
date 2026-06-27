package com.example.eduhub.marks.models;

public class GradeDetailItem {
    private String gradeId;
    private int value;
    private String type;
    private int weight;
    private String comment;
    private String teacher;
    private String createdAt;
    private boolean expanded;

    public GradeDetailItem(String gradeId, int value, String type, int weight, String comment, String teacher, String createdAt) {
        this.gradeId = gradeId;
        this.value = value;
        this.type = type;
        this.weight = weight;
        this.comment = comment;
        this.teacher = teacher;
        this.createdAt = createdAt;
    }

    public String getGradeId() { return gradeId; }
    public int getValue() { return value; }
    public String getType() { return type; }
    public int getWeight() { return weight; }
    public String getComment() { return comment; }
    public String getTeacher() { return teacher; }
    public String getCreatedAt() { return createdAt; }
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
}
