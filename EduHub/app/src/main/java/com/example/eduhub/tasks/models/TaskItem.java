package com.example.eduhub.tasks.models;

public class TaskItem {
    private String assignmentId;
    private String subjectName;
    private String description;
    private String statusText;
    private String deadlineText;
    private int statusType;
    private boolean isExpanded = false;

    public static final int STATUS_ACTIVE = 0;
    public static final int STATUS_REVIEW = 1;
    public static final int STATUS_OVERDUE = 2;
    public static final int STATUS_COMPLETED = 3;

    public TaskItem(String assignmentId, String subjectName, String description, String statusText, String deadlineText, int statusType) {
        this.assignmentId = assignmentId;
        this.subjectName = subjectName;
        this.description = description;
        this.statusText = statusText;
        this.deadlineText = deadlineText;
        this.statusType = statusType;
    }

    public String getAssignmentId() { return assignmentId; }
    public String getSubjectName() { return subjectName; }
    public String getDescription() { return description; }
    public String getStatusText() { return statusText; }
    public String getDeadlineText() { return deadlineText; }
    public int getStatusType() { return statusType; }
    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }

    public boolean hasExpandableContent() {
        boolean canSubmit = (statusType == STATUS_ACTIVE || statusType == STATUS_OVERDUE)
                && !"НА ПРОВЕРКЕ".equals(statusText)
                && !"ВЫПОЛНЕНО".equals(statusText);
        boolean submittedOrCompleted = "НА ПРОВЕРКЕ".equals(statusText) || "ВЫПОЛНЕНО".equals(statusText);
        return canSubmit || submittedOrCompleted;
    }
}
