package com.example.eduhub.domains.classes;

import java.util.HashSet;
import java.util.Set;

public class Teacher extends User {
    private String employeeId;
    private String department;
    private Set<Subject> teachingSubjects;
    private Set<StudentGroup> assignedGroups;

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Set<Subject> getTeachingSubjects() { return teachingSubjects; }
    public void setTeachingSubjects(Set<Subject> teachingSubjects) { this.teachingSubjects = teachingSubjects; }

    public Set<StudentGroup> getAssignedGroups() { return assignedGroups; }
    public void setAssignedGroups(Set<StudentGroup> assignedGroups) { this.assignedGroups = assignedGroups; }

    public Teacher() {
        super();
        this.teachingSubjects = new HashSet<>();
        this.assignedGroups = new HashSet<>();
    }

    @Override
    public UserRole getRole() {
        return UserRole.TEACHER;
    }

    
    public boolean canSetGrades(Subject subject, StudentGroup group) {
        if (!isActive) return false;
        if (!teachingSubjects.contains(subject)) return false;
        return assignedGroups.contains(group);
    }

    public boolean canSetAttendance(StudentGroup group) {
        return isActive && assignedGroups.contains(group);
    }

    public boolean canAddHomework(Subject subject, StudentGroup group) {
        return canSetGrades(subject, group);
    }

    public void addTeachingSubject(Subject subject) {
        teachingSubjects.add(subject);
    }

    public void removeTeachingSubject(Subject subject) {
        teachingSubjects.remove(subject);
    }

    public void assignGroup(StudentGroup group) {
        assignedGroups.add(group);
    }

    public void unassignGroup(StudentGroup group) {
        assignedGroups.remove(group);
    }

    public boolean teachesSubject(Subject subject) {
        return teachingSubjects.contains(subject);
    }

    public boolean isAssignedToGroup(StudentGroup group) {
        return assignedGroups.contains(group);
    }
}
