package com.example.eduhub.domains.classes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Assignment {
    private UUID id;
    private String title;
    private String description;
    private Subject subject;
    private Teacher teacher;
    private StudentGroup group;
    private LocalDate assignedDate;
    private LocalDate dueDate;
    private AssignmentType type;
    private String attachments;
    private Integer maxPoints;
    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public StudentGroup getGroup() { return group; }
    public void setGroup(StudentGroup group) { this.group = group; }

    public LocalDate getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public AssignmentType getType() { return type; }
    public void setType(AssignmentType type) { this.type = type; }

    public String getAttachments() { return attachments; }
    public void setAttachments(String attachments) { this.attachments = attachments; }

    public Integer getMaxPoints() { return maxPoints; }
    public void setMaxPoints(Integer maxPoints) { this.maxPoints = maxPoints; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Assignment() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.assignedDate = LocalDate.now();
    }

    public Assignment(String title, Subject subject, Teacher teacher, StudentGroup group, LocalDate dueDate) {
        this();
        this.title = title;
        this.subject = subject;
        this.teacher = teacher;
        this.group = group;
        this.dueDate = dueDate;
    }

    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    public boolean isDueSoon(int daysThreshold) {
        if (dueDate == null) return false;
        return LocalDate.now().plusDays(daysThreshold).isAfter(dueDate) && !isOverdue();
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public enum AssignmentType {
        HOMEWORK,           
        PRACTICAL,          
        LABORATORY,         
        ESSAY,              
        PROJECT,            
        PREPARATION         
    }
}