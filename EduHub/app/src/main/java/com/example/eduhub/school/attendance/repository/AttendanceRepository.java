package com.example.eduhub.school.attendance.repository;

import android.content.Context;
import android.database.Cursor;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.school.attendance.models.AttendanceItem;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class AttendanceRepository {

    private final DBHelper dbHelper;
    private final String studentId;

    public AttendanceRepository(Context context, String studentId) {
        this.dbHelper = DBHelper.getInstance(context);
        this.studentId = studentId;
    }

    
    public int[] getMonthlySummary(YearMonth month) {
        int present = 0, absent = 0, late = 0;
        LocalDate from = month.atDay(1);
        LocalDate to = month.atEndOfMonth();
        try (Cursor c = dbHelper.findAttendanceForStudentRaw(studentId)) {
            while (c.moveToNext()) {
                String date = c.getString(c.getColumnIndexOrThrow("date"));
                if (date == null || date.compareTo(from.toString()) < 0 || date.compareTo(to.toString()) > 0) continue;
                String status = c.getString(c.getColumnIndexOrThrow("status"));
                if (status == null) continue;
                switch (status) {
                    case "PRESENT": present++; break;
                    case "ABSENT": case "EXCUSED_ABSENT": absent++; break;
                    case "LATE": case "EARLY_LEAVE": late++; break;
                }
            }
        }
        return new int[]{present, absent, late};
    }

    
    public List<AttendanceItem> getAttendanceItemsForDate(LocalDate date) {
        List<AttendanceItem> items = new ArrayList<>();
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

        try (Cursor c = dbHelper.findAttendanceForStudentRaw(studentId)) {
            while (c.moveToNext()) {
                String rowDate = c.getString(c.getColumnIndexOrThrow("date"));
                if (rowDate == null || !rowDate.equals(dateStr)) continue;

                String subjectName = c.getString(c.getColumnIndexOrThrow("subject_name"));
                String statusRaw   = c.getString(c.getColumnIndexOrThrow("status"));
                String comment     = c.getString(c.getColumnIndexOrThrow("comment"));
                String startTime   = c.getString(c.getColumnIndexOrThrow("start_time"));
                String endTime     = c.getString(c.getColumnIndexOrThrow("end_time"));

                if (subjectName == null) subjectName = "Занятие";
                if (comment == null) comment = "";

                String timeLabel = formatLessonTime(startTime, endTime, dateStr);

                String statusText;
                int statusType;
                if (statusRaw == null) {
                    statusText = "Отсутствовал";
                    statusType = 2;
                } else {
                    switch (statusRaw) {
                        case "PRESENT":
                            statusText = "Присутствовал";
                            statusType = 0;
                            break;
                        case "LATE":
                            statusText = "Опоздал";
                            statusType = 1;
                            break;
                        case "ABSENT":
                            statusText = "Отсутствовал";
                            statusType = 2;
                            break;
                        case "EXCUSED_ABSENT":
                            statusText = "Уважительная причина";
                            statusType = 1;
                            break;
                        case "EARLY_LEAVE":
                            statusText = "Ушёл раньше";
                            statusType = 1;
                            break;
                        default:
                            statusText = statusRaw;
                            statusType = 0;
                    }
                }

                items.add(new AttendanceItem(subjectName, timeLabel, statusText, statusType, comment));
            }
        }
        return items;
    }

    
    private String formatLessonTime(String startTime, String endTime, String fallback) {
        String start = trimTime(startTime);
        String end = trimTime(endTime);
        if (start != null && end != null) return start + " \u2014 " + end;
        if (start != null) return start;
        return fallback;
    }

    private String trimTime(String t) {
        if (t == null || t.isEmpty()) return null;
        
        return t.length() >= 5 ? t.substring(0, 5) : t;
    }
}
