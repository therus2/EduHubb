package com.example.eduhub.teacher.repository;

import android.content.Context;
import android.database.Cursor;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.teacher.models.AttendanceDetailItem;
import com.example.eduhub.teacher.models.GradeDetailItem;
import com.example.eduhub.teacher.models.StudentInfoItem;

import java.util.ArrayList;
import java.util.List;

public class TeacherStudentRepository {

    private final DBHelper dbHelper;

    public TeacherStudentRepository(Context context) {
        this.dbHelper = DBHelper.getInstance(context);
    }

    public List<StudentInfoItem> getStudentsInGroup(String groupId) {
        List<StudentInfoItem> items = new ArrayList<>();
        try (Cursor c = dbHelper.findStudentsByGroup(groupId)) {
            while (c.moveToNext()) {
                String id    = c.getString(c.getColumnIndexOrThrow("student_id"));
                String name  = c.getString(c.getColumnIndexOrThrow("student_name"));
                String phone = c.getString(c.getColumnIndexOrThrow("phone_number"));
                String group = c.getString(c.getColumnIndexOrThrow("group_code"));
                items.add(new StudentInfoItem(id, name, phone != null ? phone : "", group));
            }
        }
        return items;
    }

    public List<GradeDetailItem> getGrades(String studentId) {
        List<GradeDetailItem> items = new ArrayList<>();
        try (Cursor c = dbHelper.findGradesByStudent(studentId)) {
            while (c.moveToNext()) {
                String gId    = c.getString(c.getColumnIndexOrThrow("grade_id"));
                int    value  = c.getInt(c.getColumnIndexOrThrow("value"));
                String subj   = c.getString(c.getColumnIndexOrThrow("subject_name"));
                String date   = c.getString(c.getColumnIndexOrThrow("created_at"));
                String type   = c.getString(c.getColumnIndexOrThrow("grade_type"));
                String comm   = c.getString(c.getColumnIndexOrThrow("comment"));
                if (date != null && date.length() >= 10) date = date.substring(0, 10);
                items.add(new GradeDetailItem(gId, value, subj, date, translateType(type), comm != null ? comm : ""));
            }
        }
        return items;
    }

    private String translateType(String type) {
        if (type == null) return "—";
        switch (type) {
            case "CURRENT":      return "Текущая";
            case "CONTROL":      return "Контрольная";
            case "TEST":         return "Тест";
            case "PRACTICAL":    return "Практическая";
            case "EXAM":         return "Экзамен";
            case "CREDIT":       return "Зачёт";
            case "HOMEWORK":     return "Домашняя";
            case "INDEPENDENT":  return "Самостоятельная";
            default:             return type;
        }
    }

    public List<AttendanceDetailItem> getAttendance(String studentId) {
        List<AttendanceDetailItem> items = new ArrayList<>();
        try (Cursor c = dbHelper.findAttendanceByStudent(studentId)) {
            while (c.moveToNext()) {
                String attId  = c.getString(c.getColumnIndexOrThrow("att_id"));
                String date   = c.getString(c.getColumnIndexOrThrow("date"));
                String status = c.getString(c.getColumnIndexOrThrow("status"));
                String comm   = c.getString(c.getColumnIndexOrThrow("comment"));
                String topic  = c.getString(c.getColumnIndexOrThrow("lesson_topic"));
                String subj   = c.getString(c.getColumnIndexOrThrow("subject_name"));
                items.add(new AttendanceDetailItem(attId, date, status, comm != null ? comm : "",
                        topic != null ? topic : "", subj));
            }
        }
        return items;
    }
}
