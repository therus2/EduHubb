package com.example.eduhub.teacher.models;

public class JournalStudentItem {
    private String studentId;
    private String studentName;
    private String averageMark;
    private String[] marks;
    private String[] gradeIds;

    public JournalStudentItem(String studentId, String studentName, String averageMark, String[] marks, String[] gradeIds) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.averageMark = averageMark;
        this.marks = marks;
        this.gradeIds = gradeIds;
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getAverageMark() { return averageMark; }
    public String[] getMarks() { return marks; }
    public String[] getGradeIds() { return gradeIds; }

    public void setAverageMark(String averageMark) { this.averageMark = averageMark; }
}
