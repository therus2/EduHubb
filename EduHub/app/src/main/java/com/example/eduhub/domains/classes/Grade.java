package com.example.eduhub.domains.classes;

import android.os.Build;

import java.time.LocalDateTime;
import java.util.UUID;

public class Grade {
    private UUID id;
    private Student student;
    private Subject subject;
    private Teacher teacher;
    private Integer value; 
    private GradeType type;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID lessonId;
    private Teacher updatedBy;
    private Integer weight = 1; 

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public Integer getValue() { return value; }
    

    public GradeType getType() { return type; }
    public void setType(GradeType type) { this.type = type; }

    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }

    public String getComment() { return comment; }
    
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Teacher getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Teacher updatedBy) { this.updatedBy = updatedBy; }

    public UUID getLessonId() { return lessonId; }
    public void setLessonId(UUID lessonId) { this.lessonId = lessonId; }

    public Grade() {
        this.id = UUID.randomUUID();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Grade(Student student, Subject subject, Teacher teacher, Integer value, GradeType type) {
        this();
        this.student = student;
        this.subject = subject;
        this.teacher = teacher;
        setValue(value);
        this.type = type;
    }

    public void setValue(Integer value) {
        validateGradeValue(value);
        this.value = value;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    private void validateGradeValue(Integer value) {
        if (value == null || value < 2 || value > 5) {
            throw new IllegalArgumentException("Grade value must be between 2 and 5");
        }
    }

    public void addComment(String comment) {
        this.comment = comment;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean isPassing() {
        return value != null && value >= 3;
    }

    public enum GradeType {
        CURRENT,        
        CONTROL,        
        TEST,           
        PRACTICAL,      
        EXAM,           
        CREDIT,         
        HOMEWORK,       
        ESSAY,          
        TEST_PREP,      
        LAB             
    }
}
