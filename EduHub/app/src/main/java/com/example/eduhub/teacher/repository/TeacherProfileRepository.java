package com.example.eduhub.teacher.repository;

import android.content.Context;
import android.database.Cursor;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.teacher.models.NextClassItem;
import com.example.eduhub.teacher.models.TaskToCheckItem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TeacherProfileRepository {

    private final DBHelper dbHelper;
    private final String teacherId;

    public TeacherProfileRepository(Context context, String teacherId) {
        this.dbHelper = DBHelper.getInstance(context);
        this.teacherId = teacherId;
    }

    public List<NextClassItem> getNextClasses() {
        List<NextClassItem> items = new ArrayList<>();
        LocalTime now = LocalTime.now();

        for (int offset = 0; offset <= 1; offset++) {
            if (!items.isEmpty()) break;
            LocalDate targetDate = LocalDate.now().plusDays(offset);
            int targetDOW = targetDate.getDayOfWeek().getValue();

            try (Cursor c = dbHelper.findTeacherLessonsForDayRaw(teacherId, targetDOW)) {
                while (c.moveToNext() && items.size() < 3) {
                    String startStr = c.getString(c.getColumnIndexOrThrow("start_time"));
                    String endStr = c.getString(c.getColumnIndexOrThrow("end_time"));
                    LocalTime endTime = LocalTime.parse(endStr);

                    if (offset == 0 && !endTime.isAfter(now)) continue;

                    LocalTime startTime = LocalTime.parse(startStr);
                    String lessonId = c.getString(c.getColumnIndexOrThrow("id"));
                    String subject = c.getString(c.getColumnIndexOrThrow("subject_name"));
                    String group = c.getString(c.getColumnIndexOrThrow("group_code"));
                    String classroom = c.getString(c.getColumnIndexOrThrow("classroom"));

                    String classSubject = group + " • " + subject;
                    String timeRoom = startStr + " — " + endStr + " • " + classroom;
                    String badge = (offset == 0 && !startTime.isAfter(now)) ? "ИДЕТ" : "СКОРО";

                    items.add(new NextClassItem(classSubject, timeRoom, badge,
                            lessonId, targetDate.toString()));
                }
            }
        }

        if (items.isEmpty()) {
            items.add(new NextClassItem("Нет занятий", "—", "—"));
        }
        return items;
    }

    public List<TaskToCheckItem> getTasksToCheck() {
        List<TaskToCheckItem> items = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int offset = 0; offset <= 1; offset++) {
            if (!items.isEmpty()) break;
            String date = today.plusDays(offset).toString();

            try (Cursor c = dbHelper.findAssignmentsForTeacherByDateRaw(teacherId, date)) {
                while (c.moveToNext()) {
                    String id = c.getString(c.getColumnIndexOrThrow("id"));
                    String title = c.getString(c.getColumnIndexOrThrow("title"));
                    String group = c.getString(c.getColumnIndexOrThrow("group_code"));
                    String groupId = c.getString(c.getColumnIndexOrThrow("group_id"));
                    int total = c.getInt(c.getColumnIndexOrThrow("total"));
                    int submitted = c.getInt(c.getColumnIndexOrThrow("submitted"));
                    int remaining = total - submitted;
                    int progress = total > 0 ? (submitted * 100 / total) : 0;
                    String subtitle = group + " • " + remaining + " работ осталось";

                    items.add(new TaskToCheckItem(id, title, subtitle, progress, groupId));
                }
            }
        }

        if (items.isEmpty()) {
            items.add(new TaskToCheckItem("", "Нет заданий на проверку", "—", 0));
        }
        return items;
    }
}
