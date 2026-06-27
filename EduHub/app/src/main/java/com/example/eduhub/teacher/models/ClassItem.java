package com.example.eduhub.teacher.models;

public class ClassItem {
    private String id;
    private String className;
    private String studentsCount;

    public ClassItem(String id, String className, String studentsCount) {
        this.id = id;
        this.className = className;
        this.studentsCount = studentsCount;
    }

    public String getId() { return id; }
    public String getClassName() { return className; }
    public String getStudentsCount() { return studentsCount; }
}
