package com.example.eduhub.teacher.models;

public class AssignmentWithStatsItem {
    private String id;
    private String title;
    private String description;
    private String subjectName;
    private String groupCode;
    private String dueDate;
    private int total;
    private int submitted;
    private int overdueCount;
    private int onReview;

    public AssignmentWithStatsItem(String id, String title, String description,
                                   String subjectName, String groupCode, String dueDate,
                                   int total, int submitted, int overdueCount, int onReview) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subjectName = subjectName;
        this.groupCode = groupCode;
        this.dueDate = dueDate;
        this.total = total;
        this.submitted = submitted;
        this.overdueCount = overdueCount;
        this.onReview = onReview;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSubjectName() { return subjectName; }
    public String getGroupCode() { return groupCode; }
    public String getDueDate() { return dueDate; }
    public int getTotal() { return total; }
    public int getSubmitted() { return submitted; }
    public int getOverdueCount() { return overdueCount; }
    public int getOnReview() { return onReview; }
    public int getNotSubmitted() { return total - submitted; }
    public int getProgressPercent() { return total > 0 ? (submitted * 100 / total) : 0; }
}
