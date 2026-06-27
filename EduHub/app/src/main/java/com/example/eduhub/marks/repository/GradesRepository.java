package com.example.eduhub.marks.repository;

import android.content.Context;
import android.database.Cursor;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.marks.models.GradeDetailItem;
import com.example.eduhub.marks.models.MarkDateItem;
import com.example.eduhub.marks.models.MarkSubjectItem;

import java.util.ArrayList;
import java.util.List;


public class GradesRepository {

    private final DBHelper dbHelper;
    private final String studentId;

    public GradesRepository(Context context, String studentId) {
        this.dbHelper = DBHelper.getInstance(context);
        this.studentId = studentId;
    }

    
    public List<MarkDateItem> getRecentGrades() {
        List<MarkDateItem> items = new ArrayList<>();
        try (Cursor c = dbHelper.findGradesForStudentRaw(studentId)) {
            while (c.moveToNext()) {
                int    value       = c.getInt(c.getColumnIndexOrThrow("value"));
                String type        = c.getString(c.getColumnIndexOrThrow("grade_type"));
                int    weight      = c.getInt(c.getColumnIndexOrThrow("weight"));
                String comment     = c.getString(c.getColumnIndexOrThrow("comment"));
                String subjectName = c.getString(c.getColumnIndexOrThrow("subject_name"));
                String teacherName = c.getString(c.getColumnIndexOrThrow("teacher_name"));
                String createdAt   = c.getString(c.getColumnIndexOrThrow("created_at"));

                String dateStr  = createdAt != null && createdAt.length() >= 10 ? createdAt.substring(0, 10) : "Сегодня";
                String typeDesc = "Тип: " + translateType(type) + " (Вес: " + weight + ")";

                items.add(new MarkDateItem(
                        dateStr,
                        subjectName != null ? subjectName : "Предмет",
                        typeDesc,
                        value,
                        teacherName != null ? teacherName : "—",
                        comment != null ? comment : ""
                ));
            }
        }
        return items;
    }

    
    public List<MarkSubjectItem> getSubjectAverages() {
        List<MarkSubjectItem> items = new ArrayList<>();
        try (Cursor c = dbHelper.findSubjectsWithAverageForStudent(studentId)) {
            while (c.moveToNext()) {
                String subjectId   = c.getString(c.getColumnIndexOrThrow("subject_id"));
                String subjectName = c.getString(c.getColumnIndexOrThrow("subject_name"));
                int gradeCount     = c.getInt(c.getColumnIndexOrThrow("grade_count"));
                String teacherName = c.getString(c.getColumnIndexOrThrow("teacher_full_name"));

                double avg;
                int progress;
                String statusText;

                if (gradeCount == 0) {
                    avg = 0.0;
                    progress = 0;
                    statusText = "нет оценок";
                } else {
                    avg = c.getDouble(c.getColumnIndexOrThrow("avg_grade"));
                    progress = (int) Math.round(avg / 5.0 * 100);
                    if (avg >= 4.55) {
                        statusText = "цель достигнута";
                    } else if (avg >= 4.0) {
                        statusText = "необходимо подтянуть";
                    } else {
                        statusText = "требуется особое внимание";
                    }
                }

                items.add(new MarkSubjectItem(subjectId, subjectName, avg, progress, statusText, teacherName, gradeCount));
            }
        }
        return items;
    }

    
    public List<GradeDetailItem> getGradesBySubject(String subjectId) {
        List<GradeDetailItem> items = new ArrayList<>();
        try (Cursor c = dbHelper.findGradesBySubjectForStudentRaw(studentId, subjectId)) {
            while (c.moveToNext()) {
                int    value      = c.getInt(c.getColumnIndexOrThrow("value"));
                String type       = c.getString(c.getColumnIndexOrThrow("grade_type"));
                int    weight     = c.getInt(c.getColumnIndexOrThrow("weight"));
                String comment    = c.getString(c.getColumnIndexOrThrow("comment"));
                String teacher    = c.getString(c.getColumnIndexOrThrow("teacher_name"));
                String createdAt  = c.getString(c.getColumnIndexOrThrow("created_at"));
                String gradeId    = c.getString(c.getColumnIndexOrThrow("id"));

                items.add(new GradeDetailItem(gradeId, value, translateType(type), weight,
                        comment != null ? comment : "", teacher != null ? teacher : "",
                        createdAt != null && createdAt.length() >= 16 ? createdAt.substring(0, 16) : ""));
            }
        }
        return items;
    }

    
    public int getAbsenceCount(String subjectId) {
        return dbHelper.findAbsenceCountBySubject(studentId, subjectId);
    }

    private String translateType(String type) {
        if (type == null) return "—";
        switch (type) {
            case "CURRENT":    return "Текущая";
            case "CONTROL":    return "Контрольная";
            case "TEST":       return "Тест";
            case "PRACTICAL":  return "Практическая";
            case "EXAM":       return "Экзамен";
            case "CREDIT":     return "Зачёт";
            case "HOMEWORK":   return "Домашняя";
            default:           return type;
        }
    }
}
