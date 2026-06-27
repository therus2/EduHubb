package com.example.eduhub.schedule.repository;

import android.database.Cursor;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.schedule.models.BreakItem;
import com.example.eduhub.schedule.models.DataSchedule;
import com.example.eduhub.schedule.models.LessonItem;
import com.example.eduhub.schedule.models.ScheduleItem;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;


public class ScheduleRepository {

    private static final String[] DAY_NAMES = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
    private static final String[] MONTH_NAMES = {
            "январь", "февраль", "март", "апрель", "май", "июнь",
            "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"
    };

    
    public static String getMonthName(int month) {
        if (month < 1 || month > 12) return "";
        return MONTH_NAMES[month - 1];
    }

    
    public static List<DataSchedule> getDaysForMonth(int year, int month) {
        List<DataSchedule> days = new ArrayList<>();
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        
        YearMonth prevYearMonth = yearMonth.minusMonths(1);
        int prevYear = prevYearMonth.getYear();
        int prevMonth = prevYearMonth.getMonthValue();
        int prevDaysInMonth = prevYearMonth.lengthOfMonth();

        
        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue(); 
        int prevStartDay = prevDaysInMonth - firstDayOfWeek + 2;
        for (int i = 1; i < firstDayOfWeek; i++) {
            int dayNum = prevStartDay + i - 1;
            LocalDate date = LocalDate.of(prevYear, prevMonth, dayNum);
            int dowIndex = date.getDayOfWeek().getValue() - 1;
            days.add(new DataSchedule(DAY_NAMES[dowIndex], dayNum, date, false));
        }

        
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            int dowIndex = date.getDayOfWeek().getValue() - 1;
            days.add(new DataSchedule(DAY_NAMES[dowIndex], day, date, true));
        }

        
        int remaining = days.size() % 7;
        if (remaining > 0) {
            int padCount = 7 - remaining;
            YearMonth nextYearMonth = yearMonth.plusMonths(1);
            for (int i = 1; i <= padCount; i++) {
                LocalDate date = LocalDate.of(nextYearMonth.getYear(), nextYearMonth.getMonthValue(), i);
                int dowIndex = date.getDayOfWeek().getValue() - 1;
                days.add(new DataSchedule(DAY_NAMES[dowIndex], i, date, false));
            }
        }

        return days;
    }

    
    public static List<ScheduleItem> getLessonsForDay(DBHelper dbHelper, String groupId, LocalDate date) {
        List<ScheduleItem> items = new ArrayList<>();
        if (dbHelper == null || groupId == null || date == null) return items;

        int dayOfWeek = date.getDayOfWeek().getValue(); 
        Cursor cursor = dbHelper.findScheduleForGroupDayRaw(groupId, dayOfWeek);

        if (cursor != null) {
            int colItemType = cursor.getColumnIndex("item_type");
            int colStartTime = cursor.getColumnIndex("start_time");
            int colEndTime = cursor.getColumnIndex("end_time");
            int colClassroom = cursor.getColumnIndex("classroom");
            int colSubjectId = cursor.getColumnIndex("subject_id");
            int colSubjectName = cursor.getColumnIndex("subject_name");
            int colTeacherName = cursor.getColumnIndex("teacher_name");

            int lessonCounter = 0;
            List<TimedLesson> lessons = new ArrayList<>();
            while (cursor.moveToNext()) {
                String itemType = cursor.getString(colItemType == -1 ? cursor.getColumnIndexOrThrow("item_type") : colItemType);
                if (!"lesson".equals(itemType)) {
                    continue;
                }

                lessonCounter++;
                String lessonId = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
                String startTime = cursor.getString(colStartTime == -1 ? cursor.getColumnIndexOrThrow("start_time") : colStartTime);
                String endTime = cursor.getString(colEndTime == -1 ? cursor.getColumnIndexOrThrow("end_time") : colEndTime);
                String classroom = cursor.getString(colClassroom == -1 ? cursor.getColumnIndexOrThrow("classroom") : colClassroom);
                String subjectId = cursor.getString(colSubjectId == -1 ? cursor.getColumnIndexOrThrow("subject_id") : colSubjectId);
                String subjectName = cursor.getString(colSubjectName == -1 ? cursor.getColumnIndexOrThrow("subject_name") : colSubjectName);
                String teacherName = cursor.getString(colTeacherName == -1 ? cursor.getColumnIndexOrThrow("teacher_name") : colTeacherName);

                String homework = "";
                if (subjectId != null && !subjectId.isEmpty()) {
                    try (Cursor hwCursor = dbHelper.findHomeworkForSubjectGroupByDueDate(subjectId, groupId, date.toString())) {
                        if (hwCursor.moveToFirst()) {
                            homework = hwCursor.getString(hwCursor.getColumnIndexOrThrow("description"));
                            if (homework == null) homework = "Не задано";
                        } else {
                            homework = "Не задано";
                        }
                    } catch (Exception ignored) {
                        homework = "Не задано";
                    }
                }

                String info = lessonCounter + " занятие \u2022 " + formatTime(startTime) + " \u2014 " + formatTime(endTime);
                LessonItem lesson = new LessonItem(
                        lessonId, info, classroom != null ? classroom : "",
                        subjectName != null ? subjectName : "",
                        teacherName != null ? teacherName : "",
                        homework
                );
                lessons.add(new TimedLesson(lesson, startTime, endTime));
            }
            cursor.close();
            items.addAll(insertBreaksAfterLessons(lessons));
        }
        return items;
    }

    
    public static List<ScheduleItem> getLessonsForTeacherDay(DBHelper dbHelper, String teacherId, LocalDate date) {
        List<ScheduleItem> items = new ArrayList<>();
        if (dbHelper == null || teacherId == null || date == null) return items;

        int dayOfWeek = date.getDayOfWeek().getValue();
        Cursor cursor = dbHelper.findTeacherScheduleForDayRaw(teacherId, dayOfWeek);

        if (cursor != null) {
            int colItemType = cursor.getColumnIndex("item_type");
            int colStartTime = cursor.getColumnIndex("start_time");
            int colEndTime = cursor.getColumnIndex("end_time");
            int colClassroom = cursor.getColumnIndex("classroom");
            int colSubjectName = cursor.getColumnIndex("subject_name");
            int colGroupCode = cursor.getColumnIndex("group_code");
            int colGroupId = cursor.getColumnIndex("group_id");
            int colSubjectId = cursor.getColumnIndex("subject_id");

            int lessonCounter = 0;
            List<TimedLesson> lessons = new ArrayList<>();
            while (cursor.moveToNext()) {
                String itemType = cursor.getString(colItemType != -1 ? colItemType : cursor.getColumnIndexOrThrow("item_type"));
                if (!"lesson".equals(itemType)) {
                    continue;
                }

                lessonCounter++;
                String lessonId = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String startTime = cursor.getString(colStartTime != -1 ? colStartTime : cursor.getColumnIndexOrThrow("start_time"));
                String endTime = cursor.getString(colEndTime != -1 ? colEndTime : cursor.getColumnIndexOrThrow("end_time"));
                String classroom = cursor.getString(colClassroom != -1 ? colClassroom : cursor.getColumnIndexOrThrow("classroom"));
                String subjectName = cursor.getString(colSubjectName != -1 ? colSubjectName : cursor.getColumnIndexOrThrow("subject_name"));
                String groupCode = cursor.getString(colGroupCode != -1 ? colGroupCode : cursor.getColumnIndexOrThrow("group_code"));
                String groupId = colGroupId != -1 ? cursor.getString(colGroupId) : null;
                String subjectId = colSubjectId != -1 ? cursor.getString(colSubjectId) : null;

                String info = lessonCounter + " занятие \u2022 " + formatTime(startTime) + " \u2014 " + formatTime(endTime);
                int gradeCount = dbHelper.countGradesForLessonDay(lessonId, groupId, subjectId, date.toString());
                if (gradeCount > 0) {
                    info += " \u2022 " + gradeCount + " " + pluralGrades(gradeCount);
                }
                String title = groupCode + " \u2022 " + subjectName;
                LessonItem lesson = new LessonItem(lessonId, info, classroom != null ? classroom : "", title, "", "");
                lessons.add(new TimedLesson(lesson, startTime, endTime));
            }
            cursor.close();
            items.addAll(insertBreaksAfterLessons(lessons));
        }
        return items;
    }

    private static class TimedLesson {
        private final LessonItem item;
        private final String startTime;
        private final String endTime;

        private TimedLesson(LessonItem item, String startTime, String endTime) {
            this.item = item;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    private static List<ScheduleItem> insertBreaksAfterLessons(List<TimedLesson> lessons) {
        List<ScheduleItem> result = new ArrayList<>();
        for (int i = 0; i < lessons.size(); i++) {
            TimedLesson current = lessons.get(i);
            result.add(current.item);
            if (i >= lessons.size() - 1) {
                continue;
            }
            TimedLesson next = lessons.get(i + 1);
            String breakStart = formatTime(current.endTime);
            String breakEnd = formatTime(next.startTime);
            if (breakStart.equals(breakEnd)) {
                continue;
            }
            result.add(new BreakItem(formatBreakText(null, breakStart, breakEnd)));
        }
        return result;
    }

    
    private static String formatTime(String time) {
        if (time == null || time.length() < 5) return time != null ? time : "";
        return time.substring(0, 5);
    }

    private static String formatBreakText(String label, String startTime, String endTime) {
        String start = formatTime(startTime);
        String end = formatTime(endTime);
        String resolvedLabel = resolveBreakLabel(label, start);
        return resolvedLabel + " (" + start + "-" + end + ")";
    }

    private static String resolveBreakLabel(String label, String startTime) {
        if ("12:10".equals(startTime)) {
            return "Большая перемена";
        }
        return "Перемена";
    }

    private static String pluralGrades(int n) {
        int mod10 = n % 10;
        int mod100 = n % 100;
        if (mod100 >= 11 && mod100 <= 14) return "оценок";
        if (mod10 == 1) return "оценка";
        if (mod10 >= 2 && mod10 <= 4) return "оценки";
        return "оценок";
    }
}