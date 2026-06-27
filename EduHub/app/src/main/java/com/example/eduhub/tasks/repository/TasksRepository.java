package com.example.eduhub.tasks.repository;

import android.content.Context;
import android.database.Cursor;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.tasks.models.TaskItem;

import java.util.ArrayList;
import java.util.List;

public class TasksRepository {

    private final DBHelper dbHelper;
    private final String studentId;

    public TasksRepository(Context context, String studentId) {
        this.dbHelper = DBHelper.getInstance(context);
        this.studentId = studentId;
    }

    public List<TaskItem> getAllTasks() {
        List<TaskItem> items = new ArrayList<>();
        try (Cursor c = dbHelper.findAssignmentsForStudentRaw(studentId)) {
            while (c.moveToNext()) {
                items.add(cursorToTaskItem(c));
            }
        }
        return items;
    }

    public List<TaskItem> getActiveTasks() {
        List<TaskItem> items = new ArrayList<>();
        for (TaskItem t : getAllTasks()) {
            if (t.getStatusType() == TaskItem.STATUS_ACTIVE) {
                items.add(t);
            }
        }
        return items;
    }

    public List<TaskItem> getCompletedTasks() {
        List<TaskItem> items = new ArrayList<>();
        for (TaskItem t : getAllTasks()) {
            if (t.getStatusType() == TaskItem.STATUS_COMPLETED || t.getStatusType() == TaskItem.STATUS_REVIEW) {
                items.add(t);
            }
        }
        return items;
    }

    public List<TaskItem> getOverdueTasks() {
        List<TaskItem> items = new ArrayList<>();
        for (TaskItem t : getAllTasks()) {
            if (t.getStatusType() == TaskItem.STATUS_OVERDUE) {
                items.add(t);
            }
        }
        return items;
    }

    public boolean submitHomework(String assignmentId, String comment) {
        return dbHelper.updateHomeworkCompletion(assignmentId, studentId, "SUBMITTED", comment);
    }

    private TaskItem cursorToTaskItem(Cursor c) {
        String assignmentId = c.getString(c.getColumnIndexOrThrow("id"));
        String subject    = c.getString(c.getColumnIndexOrThrow("subject_name"));
        String title      = c.getString(c.getColumnIndexOrThrow("title"));
        String description = c.getString(c.getColumnIndexOrThrow("description"));
        String dueDate    = c.getString(c.getColumnIndexOrThrow("due_date"));
        String status     = c.getString(c.getColumnIndexOrThrow("completion_status"));
        String submittedAt = c.getString(c.getColumnIndexOrThrow("submitted_at"));

        String displayDesc = (description != null && !description.isEmpty()) ? description : title;
        String deadlineText = formatDeadline(dueDate, submittedAt);
        String statusLabel;
        int statusType;

        switch (status) {
            case "IN_PROGRESS":
                statusLabel = "В ПРОЦЕССЕ";
                statusType  = TaskItem.STATUS_ACTIVE;
                break;
            case "SUBMITTED":
                statusLabel = "НА ПРОВЕРКЕ";
                statusType  = TaskItem.STATUS_REVIEW;
                break;
            case "COMPLETED":
                statusLabel = "ВЫПОЛНЕНО";
                statusType  = TaskItem.STATUS_COMPLETED;
                break;
            case "NOT_STARTED":
            default:
                statusLabel = isOverdue(dueDate) ? "ПРОСРОЧЕНО" : "В ПРОЦЕССЕ";
                statusType  = isOverdue(dueDate) ? TaskItem.STATUS_OVERDUE : TaskItem.STATUS_ACTIVE;
                break;
        }

        return new TaskItem(assignmentId, subject, displayDesc, statusLabel, deadlineText, statusType);
    }

    private boolean isOverdue(String dueDateStr) {
        if (dueDateStr == null) return false;
        try {
            java.time.LocalDate due = java.time.LocalDate.parse(dueDateStr);
            return due.isBefore(java.time.LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    private String formatDeadline(String dueDate, String submittedAt) {
        if (submittedAt != null && !submittedAt.isEmpty()) {
            String d = submittedAt.length() >= 10 ? submittedAt.substring(0, 10) : submittedAt;
            return "Сдано " + d;
        }
        if (dueDate == null) return "\u2014";
        try {
            java.time.LocalDate due  = java.time.LocalDate.parse(dueDate);
            java.time.LocalDate now  = java.time.LocalDate.now();
            long days = java.time.temporal.ChronoUnit.DAYS.between(now, due);
            if (days == 0)  return "До сегодня";
            if (days == 1)  return "До завтра";
            if (days < 0)   return "Просрочено " + dueDate;
            return "До " + dueDate;
        } catch (Exception e) {
            return dueDate;
        }
    }
}
