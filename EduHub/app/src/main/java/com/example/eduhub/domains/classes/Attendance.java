package com.example.eduhub.domains.classes;

import android.os.Build;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Attendance {
    private UUID id;
    private Student student;
    private LocalDate date;
    private AttendanceStatus status;
    private UUID lessonId;
    private String comment;
    private LocalDateTime markedAt;
    private Teacher markedBy;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public UUID getLessonId() { return lessonId; }
    public void setLessonId(UUID lessonId) { this.lessonId = lessonId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getMarkedAt() { return markedAt; }
    public void setMarkedAt(LocalDateTime markedAt) { this.markedAt = markedAt; }

    public Teacher getMarkedBy() { return markedBy; }
    public void setMarkedBy(Teacher markedBy) { this.markedBy = markedBy; }

    public Attendance() {
        this.id = UUID.randomUUID();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.markedAt = LocalDateTime.now();
        }
    }

    public Attendance(Student student, LocalDate date, AttendanceStatus status) {
        this();
        this.student = student;
        this.date = date;
        this.status = status;
    }

    public void markStatus(AttendanceStatus status) {
        this.status = status;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.markedAt = LocalDateTime.now();
        }
    }

    public boolean isAbsent() {
        return status == AttendanceStatus.ABSENT ||
                status == AttendanceStatus.LATE ||
                status == AttendanceStatus.EXCUSED_ABSENT;
    }

    public enum AttendanceStatus {
        PRESENT,            
        ABSENT,             
        LATE,               
        EXCUSED_ABSENT,     
        EARLY_LEAVE         
    }
}