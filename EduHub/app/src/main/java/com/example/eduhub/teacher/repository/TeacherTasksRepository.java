package com.example.eduhub.teacher.repository;

import android.content.Context;
import android.database.Cursor;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.teacher.models.AssignmentWithStatsItem;
import com.example.eduhub.teacher.models.SubmissionItem;

import java.util.ArrayList;
import java.util.List;

public class TeacherTasksRepository {

    private final DBHelper dbHelper;
    private final String teacherId;

    public TeacherTasksRepository(Context context, String teacherId) {
        this.dbHelper = DBHelper.getInstance(context);
        this.teacherId = teacherId;
    }

    public List<AssignmentWithStatsItem> getAssignmentsForGroup(String groupId) {
        List<AssignmentWithStatsItem> items = new ArrayList<>();
        try (Cursor c = dbHelper.findAssignmentsForTeacherAndGroupRaw(teacherId, groupId)) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow("id"));
                String title = c.getString(c.getColumnIndexOrThrow("title"));
                String description = c.getString(c.getColumnIndexOrThrow("description"));
                String subjectName = c.getString(c.getColumnIndexOrThrow("subject_name"));
                String groupCode = c.getString(c.getColumnIndexOrThrow("group_code"));
                String dueDate = c.getString(c.getColumnIndexOrThrow("due_date"));
                int total = c.getInt(c.getColumnIndexOrThrow("total"));
                int submitted = c.getInt(c.getColumnIndexOrThrow("submitted"));
                int overdueCount = c.getInt(c.getColumnIndexOrThrow("overdue_count"));
                int onReview = c.getInt(c.getColumnIndexOrThrow("on_review"));

                items.add(new AssignmentWithStatsItem(id, title, description, subjectName, groupCode, dueDate, total, submitted, overdueCount, onReview));
            }
        }
        return items;
    }

    public List<SubmissionItem> getSubmissionsForAssignment(String assignmentId) {
        List<SubmissionItem> items = new ArrayList<>();
        try (Cursor c = dbHelper.findSubmissionsForAssignmentWithDetailsRaw(assignmentId)) {
            while (c.moveToNext()) {
                String completionId = c.getString(c.getColumnIndexOrThrow("completion_id"));
                String studentId = c.getString(c.getColumnIndexOrThrow("student_id"));
                String studentName = c.getString(c.getColumnIndexOrThrow("student_name"));
                String status = c.getString(c.getColumnIndexOrThrow("status"));
                if (completionId == null || completionId.isEmpty()) {
                    completionId = studentId + ":" + assignmentId;
                }
                String submittedAt = c.getString(c.getColumnIndexOrThrow("submitted_at"));
                int idxPoints = c.getColumnIndexOrThrow("received_points");
                Integer points = c.isNull(idxPoints) ? null : c.getInt(idxPoints);
                String comment = c.getString(c.getColumnIndexOrThrow("student_comment"));

                boolean isSubmitted = "SUBMITTED".equals(status) || "COMPLETED".equals(status);
                String time = (submittedAt != null && !submittedAt.isEmpty())
                        ? submittedAt.substring(0, 16).replace("T", " ")
                        : "";

                items.add(new SubmissionItem(completionId, studentId, studentName, time, status, isSubmitted, points, comment));
            }
        }
        return items;
    }

    public List<String> getGroups() {
        List<String> groups = new ArrayList<>();
        try (Cursor c = dbHelper.findGroupsForTeacherRaw(teacherId)) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow("id"));
                String code = c.getString(c.getColumnIndexOrThrow("code"));
                groups.add(id + "|" + code);
            }
        }
        return groups;
    }

    public boolean updateAssignment(String assignmentId, String title, String dueDate) {
        return dbHelper.updateAssignmentTitleAndDue(assignmentId, title, dueDate);
    }

    public boolean deleteAssignment(String assignmentId) {
        return dbHelper.deleteAssignmentById(assignmentId);
    }

    public boolean gradeSubmission(String completionId, String status, int points) {
        return dbHelper.gradeHomeworkCompletion(completionId, status, points, null);
    }

    public boolean gradeSubmissionWithGrade(String completionId, String studentId, String assignmentId,
                                             int gradeValue, String comment) {
        return dbHelper.gradeHomeworkWithGrade(completionId, studentId, assignmentId, gradeValue, comment);
    }

    public void close() {
        dbHelper.close();
    }
}
