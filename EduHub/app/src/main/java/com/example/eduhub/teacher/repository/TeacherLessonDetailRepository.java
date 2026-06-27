package com.example.eduhub.teacher.repository;

import android.content.Context;
import android.database.Cursor;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.teacher.models.GradeEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TeacherLessonDetailRepository {

    private final DBHelper dbHelper;

    public TeacherLessonDetailRepository(Context context) {
        this.dbHelper = DBHelper.getInstance(context);
    }

    
    public String[] getLessonInfo(String lessonId) {
        try (Cursor c = dbHelper.findLessonByIdRaw(lessonId)) {
            if (c.moveToFirst()) {
                String startTime = formatTime(c.getString(c.getColumnIndexOrThrow("start_time")));
                String endTime = formatTime(c.getString(c.getColumnIndexOrThrow("end_time")));
                int topicIdx = c.getColumnIndex("lesson_topic");
                String topic = topicIdx >= 0 ? c.getString(topicIdx) : null;
                return new String[]{
                        c.getString(c.getColumnIndexOrThrow("subject_name")),
                        c.getString(c.getColumnIndexOrThrow("group_code")),
                        startTime, endTime,
                        c.getString(c.getColumnIndexOrThrow("classroom")),
                        c.getString(c.getColumnIndexOrThrow("group_id")),
                        c.getString(c.getColumnIndexOrThrow("subject_id")),
                        c.getString(c.getColumnIndexOrThrow("day_of_week")),
                        topic != null ? topic : ""
                };
            }
        }
        return new String[0];
    }

    
    public void saveLessonTopic(String lessonId, String topic) {
        dbHelper.updateLessonTopic(lessonId, topic);
    }

    
    public List<String[]> getStudents(String groupId) {
        List<String[]> list = new ArrayList<>();
        try (Cursor c = dbHelper.findStudentsInGroupRaw(groupId)) {
            while (c.moveToNext()) {
                list.add(new String[]{
                        c.getString(c.getColumnIndexOrThrow("student_id")),
                        c.getString(c.getColumnIndexOrThrow("student_name"))
                });
            }
        }
        return list;
    }

    
    public Map<String, String> getAttendanceMap(String lessonId, String date) {
        Map<String, String> map = new HashMap<>();
        try (Cursor c = dbHelper.findAttendanceByLessonAndDateRaw(lessonId, date)) {
            while (c.moveToNext()) {
                map.put(c.getString(c.getColumnIndexOrThrow("student_id")),
                        c.getString(c.getColumnIndexOrThrow("status")));
            }
        }
        return map;
    }

    
    public void saveAttendance(String lessonId, String date, Map<String, String> studentStatuses, String teacherId) {
        for (Map.Entry<String, String> e : studentStatuses.entrySet()) {
            String status = e.getValue();
            if (status == null || status.isEmpty()) continue;
            dbHelper.upsertAttendance(lessonId, date, e.getKey(), status, "", teacherId);
        }
    }

    
    public String getCurrentHomework(String subjectId, String groupId, String date) {
        try (Cursor c = dbHelper.findHomeworkForSubjectGroupByDate(subjectId, groupId, date)) {
            if (c.moveToFirst()) return c.getString(c.getColumnIndexOrThrow("description"));
        }
        return null;
    }

    
    public void saveHomework(String subjectId, String groupId, String teacherId,
                             String description, String assignedDate, String dueDate) {
        String title = description.length() > 60 ? description.substring(0, 60) + "..." : description;
        String assignmentId = dbHelper.insertAssignment(
                title,
                description,
                subjectId,
                teacherId,
                groupId,
                assignedDate,
                dueDate,
                "HOMEWORK",
                10
        );

        if (assignmentId != null) {
            dbHelper.createCompletionsForAssignment(assignmentId, groupId);
        }

        String subjectName = dbHelper.findSubjectNameById(subjectId);
        if (subjectName == null) subjectName = "Предмет";
        String notifTitle = "Новое домашнее задание";
        String message = subjectName + ": " + description;
        for (String uid : dbHelper.findStudentUserIdsInGroup(groupId)) {
            dbHelper.insertNotification(uid, notifTitle, message, "NEW_HOMEWORK");
        }
    }

    
    public String getNextLessonDate(String lessonId) {
        String[] info = getLessonInfo(lessonId);
        if (info.length < 8) return LocalDate.now().toString();
        try {
            int dayOfWeek = Integer.parseInt(info[7]);
            LocalDate today = LocalDate.now();
            int todayDOW = today.getDayOfWeek().getValue();
            int diff = dayOfWeek - todayDOW;
            if (diff <= 0) diff += 7;
            return today.plusDays(diff).toString();
        } catch (NumberFormatException e) {
            return LocalDate.now().toString();
        }
    }

    
    public Map<String, List<GradeEntry>> getGradeEntries(String lessonId) {
        return getGradeEntries(lessonId, null, null, null);
    }

    
    public Map<String, List<GradeEntry>> getGradeEntries(String lessonId, String groupId,
                                                         String subjectId, String date) {
        Map<String, List<GradeEntry>> map = new HashMap<>();
        Set<String> seen = new HashSet<>();
        mergeGradeCursor(map, seen, dbHelper.findGradesByLessonWithDetailsRaw(lessonId));
        if (groupId != null && subjectId != null && date != null && !date.isEmpty()) {
            mergeGradeCursor(map, seen, dbHelper.findGradesForGroupSubjectOnDateRaw(groupId, subjectId, date));
        }
        return map;
    }

    private void mergeGradeCursor(Map<String, List<GradeEntry>> map, Set<String> seen, Cursor c) {
        if (c == null) return;
        try {
            while (c.moveToNext()) {
                String gradeId = c.getString(c.getColumnIndexOrThrow("id"));
                if (seen.contains(gradeId)) continue;
                seen.add(gradeId);

                String studentId = c.getString(c.getColumnIndexOrThrow("student_id"));
                int value = c.getInt(c.getColumnIndexOrThrow("value"));
                String type = c.getString(c.getColumnIndexOrThrow("grade_type"));
                String date = c.getString(c.getColumnIndexOrThrow("created_at"));
                if (date != null && date.length() >= 10) date = date.substring(0, 10);
                String comment = c.getString(c.getColumnIndexOrThrow("comment"));
                String displayValue = formatGradeValueForUi(value, type);
                map.computeIfAbsent(studentId, k -> new ArrayList<>())
                        .add(new GradeEntry(studentId, displayValue, type, date,
                                comment != null ? comment : ""));
            }
        } finally {
            c.close();
        }
    }

    private static String formatGradeValueForUi(int value, String type) {
        if (type != null) {
            switch (type) {
                case "ABSENT":
                    return "\u041D";
                case "EXEMPT":
                    return "\u0411";
                case "CREDIT_PASS":
                    return "\u0417\u0430\u0447";
                default:
                    break;
            }
        }
        if (value >= 2 && value <= 5) return String.valueOf(value);
        if (value > 0) return String.valueOf(value);
        return "";
    }

    
    public void saveGradeEntries(String lessonId, String subjectId, String teacherId,
                                 Map<String, List<GradeEntry>> entriesByStudent) {
        for (Map.Entry<String, List<GradeEntry>> e : entriesByStudent.entrySet()) {
            String studentId = e.getKey();
            dbHelper.deleteGradeForLesson(lessonId, studentId);
            for (GradeEntry g : e.getValue()) {
                String val = g.getGradeValue();
                if (val == null || val.isEmpty()) continue;
                try {
                    int v = Integer.parseInt(val);
                    if (v < 2 || v > 5) continue;
                    String type = g.getGradeType() != null ? g.getGradeType() : "CURRENT";
                    String date = g.getGradeDate();
                    if (date != null && date.length() >= 10) {
                        
                        dbHelper.insertGradeForLesson(studentId, subjectId, teacherId,
                                v, type, g.getComment(), lessonId, date.substring(0, 10));
                    } else {
                        dbHelper.insertGradeForLesson(studentId, subjectId, teacherId,
                                v, type, lessonId, g.getComment());
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    public void close() {
        dbHelper.close();
    }

    private String formatTime(String time) {
        if (time == null || time.length() < 5) return time != null ? time : "";
        return time.substring(0, 5);
    }
}
