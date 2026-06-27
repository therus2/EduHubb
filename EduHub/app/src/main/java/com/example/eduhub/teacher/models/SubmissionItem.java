package com.example.eduhub.teacher.models;

public class SubmissionItem {
    private String completionId;
    private String studentId;
    private String studentName;
    private String submissionTime;
    private String status;
    private boolean isSubmitted;
    private Integer receivedPoints;
    private String studentComment;

    public SubmissionItem(String completionId, String studentId, String studentName,
                          String submissionTime, String status, boolean isSubmitted,
                          Integer receivedPoints, String studentComment) {
        this.completionId = completionId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.submissionTime = submissionTime;
        this.status = status;
        this.isSubmitted = isSubmitted;
        this.receivedPoints = receivedPoints;
        this.studentComment = studentComment;
    }

    public String getCompletionId() { return completionId; }
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getSubmissionTime() { return submissionTime; }
    public String getStatus() { return status; }
    public boolean isSubmitted() { return isSubmitted; }
    public Integer getReceivedPoints() { return receivedPoints; }
    public String getStudentComment() { return studentComment; }
}
