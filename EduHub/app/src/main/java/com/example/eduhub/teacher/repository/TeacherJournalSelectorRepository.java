package com.example.eduhub.teacher.repository;

import android.content.Context;
import android.database.Cursor;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.teacher.models.ClassItem;
import com.example.eduhub.teacher.models.SubjectItem;

import java.util.ArrayList;
import java.util.List;

public class TeacherJournalSelectorRepository {

    private final DBHelper dbHelper;

    public TeacherJournalSelectorRepository(Context context) {
        this.dbHelper = DBHelper.getInstance(context);
    }

    public List<ClassItem> getClasses(String teacherId) {
        List<ClassItem> items = new ArrayList<>();
        try (Cursor c = dbHelper.findGroupsForTeacherRaw(teacherId)) {
            while (c.moveToNext()) {
                String id    = c.getString(c.getColumnIndexOrThrow("id"));
                String code  = c.getString(c.getColumnIndexOrThrow("code"));
                int    count = c.getInt(c.getColumnIndexOrThrow("students_count"));
                items.add(new ClassItem(id, code, count + " учеников"));
            }
        }
        if (items.isEmpty()) {
            items.add(new ClassItem("", "Нет групп", "—"));
        }
        return items;
    }

    public List<SubjectItem> getSubjects(String teacherId) {
        List<SubjectItem> items = new ArrayList<>();
        try (Cursor c = dbHelper.findSubjectsForTeacherRaw(teacherId)) {
            while (c.moveToNext()) {
                String id   = c.getString(c.getColumnIndexOrThrow("id"));
                String name = c.getString(c.getColumnIndexOrThrow("name"));
                String desc = c.getString(c.getColumnIndexOrThrow("description"));
                items.add(new SubjectItem(id, name, desc != null ? desc : "Основная программа"));
            }
        }
        if (items.isEmpty()) {
            items.add(new SubjectItem("", "Нет предметов", "—"));
        }
        return items;
    }
}
