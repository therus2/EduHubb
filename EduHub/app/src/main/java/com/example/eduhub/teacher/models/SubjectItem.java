package com.example.eduhub.teacher.models;

public class SubjectItem {
    private String id;
    private String subjectName;
    private String programType;

    public SubjectItem(String id, String subjectName, String programType) {
        this.id = id;
        this.subjectName = subjectName;
        this.programType = programType;
    }

    public String getId() { return id; }
    public String getSubjectName() { return subjectName; }
    public String getProgramType() { return programType; }
}
