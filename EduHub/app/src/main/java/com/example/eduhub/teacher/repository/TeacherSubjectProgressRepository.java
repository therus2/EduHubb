package com.example.eduhub.teacher.repository;

import android.content.Context;
import android.database.Cursor;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.teacher.models.LessonTopicItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TeacherSubjectProgressRepository {

    private final DBHelper dbHelper;
    private final String subjectId;

    public TeacherSubjectProgressRepository(Context context, String subjectId) {
        this.dbHelper = DBHelper.getInstance(context);
        this.subjectId = subjectId;
    }

    public List<LessonTopicItem> getLessonsForGroup(String groupId) {
        List<LessonTopicItem> items = new ArrayList<>();
        try (Cursor c = dbHelper.findLessonsForGroupSubject(groupId, subjectId)) {
            while (c.moveToNext()) {
                items.add(cursorToItem(c));
            }
        }
        return items;
    }

    public Map<String, List<LessonTopicItem>> getLessonsByGroup() {
        Map<String, List<LessonTopicItem>> result = new LinkedHashMap<>();
        try (Cursor c = dbHelper.findAllLessonsForSubjectRaw(subjectId)) {
            while (c.moveToNext()) {
                String groupCode = c.getString(c.getColumnIndexOrThrow("group_code"));
                result.computeIfAbsent(groupCode, k -> new ArrayList<>()).add(cursorToItem(c));
            }
        }
        return result;
    }

    private LessonTopicItem cursorToItem(Cursor c) {
        String id        = c.getString(c.getColumnIndexOrThrow("id"));
        int dow          = c.getInt(c.getColumnIndexOrThrow("day_of_week"));
        String start     = c.getString(c.getColumnIndexOrThrow("start_time"));
        String end       = c.getString(c.getColumnIndexOrThrow("end_time"));
        String room      = c.getString(c.getColumnIndexOrThrow("classroom"));
        String type      = c.getString(c.getColumnIndexOrThrow("lesson_type"));
        String topic     = c.getString(c.getColumnIndexOrThrow("lesson_topic"));
        String groupCode = c.getString(c.getColumnIndexOrThrow("group_code"));
        return new LessonTopicItem(id, topic != null ? topic : "", groupCode,
                dow, start, end, room != null ? room : "", type != null ? type : "");
    }
}
