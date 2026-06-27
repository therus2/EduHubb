package com.example.eduhub.domains.classes;

import java.time.LocalDateTime;
import java.util.UUID;

public class HomeworkCompletion {
    private UUID id;
    private Assignment assignment;
    private Student student;
    private CompletionStatus status;
    private String studentComment;
    private String attachments;
    private LocalDateTime submittedAt;
    private Integer receivedPoints;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public CompletionStatus getStatus() { return status; }
    public void setStatus(CompletionStatus status) { this.status = status; }

    public String getStudentComment() { return studentComment; }
    public void setStudentComment(String studentComment) { this.studentComment = studentComment; }

    public String getAttachments() { return attachments; }
    public void setAttachments(String attachments) { this.attachments = attachments; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public Integer getReceivedPoints() { return receivedPoints; }
    public void setReceivedPoints(Integer receivedPoints) { this.receivedPoints = receivedPoints; }

    public HomeworkCompletion() {
        this.id = UUID.randomUUID();
        this.status = CompletionStatus.NOT_STARTED;
    }

    public HomeworkCompletion(Assignment assignment, Student student) {
        this();
        this.assignment = assignment;
        this.student = student;
    }

    public void markAsCompleted() {
        this.status = CompletionStatus.COMPLETED;
        this.submittedAt = LocalDateTime.now();
    }

    public void markAsInProgress() {
        this.status = CompletionStatus.IN_PROGRESS;
    }

    public void markAsNotStarted() {
        this.status = CompletionStatus.NOT_STARTED;
    }

    public boolean isCompleted() {
        return status == CompletionStatus.COMPLETED;
    }

    public boolean isOverdue() {
        return assignment.isOverdue() && !isCompleted();
    }

    public enum CompletionStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        SUBMITTED
    }
}