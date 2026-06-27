package com.example.eduhub.domains.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StudentGroup {
    private UUID id;
    private String code;
    private String name;
    private int courseNumber;
    private String specialization;
    private int maxStudents;
    private List<UUID> studentIds;
    private Map<UUID, String> teacherSubjects; 

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCourseNumber() { return courseNumber; }
    public void setCourseNumber(int courseNumber) { this.courseNumber = courseNumber; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public int getMaxStudents() { return maxStudents; }
    public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }

    public List<UUID> getStudentIds() { return studentIds; }
    public void setStudentIds(List<UUID> studentIds) { this.studentIds = studentIds; }

    public Map<UUID, String> getTeacherSubjects() { return teacherSubjects; }
    public void setTeacherSubjects(Map<UUID, String> teacherSubjects) { this.teacherSubjects = teacherSubjects; }

    public StudentGroup() {
        this.id = UUID.randomUUID();
        this.studentIds = new ArrayList<>();
        this.teacherSubjects = new HashMap<>();
    }

    public StudentGroup(String code, String name, int courseNumber) {
        this();
        this.code = code;
        this.name = name;
        this.courseNumber = courseNumber;
    }

    public void addStudent(UUID studentId) {
        if (studentIds.size() < maxStudents) {
            studentIds.add(studentId);
        } else {
            throw new IllegalStateException("Group is full");
        }
    }

    public void removeStudent(UUID studentId) {
        studentIds.remove(studentId);
    }

    public boolean hasStudent(UUID studentId) {
        return studentIds.contains(studentId);
    }

    public int getCurrentStudentCount() {
        return studentIds.size();
    }

    public void assignTeacher(UUID teacherId, String subjectCode) {
        teacherSubjects.put(teacherId, subjectCode);
    }

    public void unassignTeacher(UUID teacherId) {
        teacherSubjects.remove(teacherId);
    }

    public boolean isTeacherAssigned(UUID teacherId) {
        return teacherSubjects.containsKey(teacherId);
    }
}
