package com.example.eduhub.teacher.models;

public class StudentInfoItem {
    private String studentId;
    private String studentName;
    private String phoneNumber;
    private String groupCode;

    public StudentInfoItem(String studentId, String studentName, String phoneNumber, String groupCode) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.phoneNumber = phoneNumber;
        this.groupCode = groupCode;
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getGroupCode() { return groupCode; }
}
