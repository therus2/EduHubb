package com.example.eduhub.marks.models;

public class MarkSubjectItem {
    private String subjectId;
    private String subjectName;
    private double average;
    private int progress;
    private String statusText;
    private String teacherName;
    private int gradeCount;

    public MarkSubjectItem(String subjectId, String subjectName, double average, int progress, String statusText, String teacherName, int gradeCount) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.average = average;
        this.progress = progress;
        this.statusText = statusText;
        this.teacherName = teacherName;
        this.gradeCount = gradeCount;
    }

    public String getSubjectId() { return subjectId; }
    public String getSubjectName() { return subjectName; }
    public double getAverage() { return average; }
    public int getProgress() { return progress; }
    public String getStatusText() { return statusText; }
    public String getTeacherName() { return teacherName; }
    public int getGradeCount() { return gradeCount; }
}
