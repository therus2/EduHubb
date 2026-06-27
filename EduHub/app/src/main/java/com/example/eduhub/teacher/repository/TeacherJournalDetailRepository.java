package com.example.eduhub.teacher.repository;

import android.content.Context;
import android.database.Cursor;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.teacher.models.JournalStudentItem;
import com.example.eduhub.utils.GradeDisplayHelper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TeacherJournalDetailRepository {

    private final DBHelper dbHelper;
    private final String groupId;
    private final String subjectId;

    public TeacherJournalDetailRepository(Context context, String groupId, String subjectId) {
        this.dbHelper = DBHelper.getInstance(context);
        this.groupId = groupId;
        this.subjectId = subjectId;
    }

    public List<LessonDayInfo> getLessonDays() {
        return loadScheduledLessonDays();
    }

    
    public List<LessonDayInfo> getLessonDaysForWeek(LocalDate weekStart) {
        List<LessonDayInfo> days = loadScheduledLessonDays();
        Set<String> columnKeys = new HashSet<>();
        for (LessonDayInfo day : days) {
            LocalDate date = weekStart.with(DayOfWeek.of(day.dayOfWeek));
            columnKeys.add(date + "|" + day.lessonId);
        }

        String weekStartStr = weekStart.toString();
        String weekEndStr = weekStart.plusDays(7).toString();
        List<String> lessonIds = new ArrayList<>();
        List<String> fullDates = new ArrayList<>();
        for (LessonDayInfo day : days) {
            lessonIds.add(day.lessonId);
            fullDates.add(weekStart.with(DayOfWeek.of(day.dayOfWeek)).toString());
        }

        try (Cursor c = dbHelper.findGradesForGroupSubjectRaw(groupId, subjectId)) {
            while (c.moveToNext()) {
                String lessonId = c.getString(c.getColumnIndexOrThrow("lesson_id"));
                String dateStr = c.getString(c.getColumnIndexOrThrow("created_at"));
                String gradeDate = extractDate(dateStr);
                if (gradeDate == null) continue;
                if (gradeDate.compareTo(weekStartStr) < 0 || gradeDate.compareTo(weekEndStr) >= 0) {
                    continue;
                }
                if (resolveColumnIndex(lessonId, gradeDate, lessonIds, fullDates) >= 0) {
                    continue;
                }

                LocalDate date = LocalDate.parse(gradeDate);
                int dow = date.getDayOfWeek().getValue();
                String colLessonId = lessonId;
                if (colLessonId == null || colLessonId.isEmpty()) {
                    colLessonId = dbHelper.findFirstLessonIdForGroupSubjectDay(groupId, subjectId, dow);
                }
                String key = gradeDate + "|" + colLessonId;
                if (columnKeys.add(key)) {
                    days.add(new LessonDayInfo(dow, colLessonId));
                    lessonIds.add(colLessonId);
                    fullDates.add(gradeDate);
                }
            }
        }

        days.sort((a, b) -> {
            LocalDate da = weekStart.with(DayOfWeek.of(a.dayOfWeek));
            LocalDate db = weekStart.with(DayOfWeek.of(b.dayOfWeek));
            int cmp = da.compareTo(db);
            if (cmp != 0) return cmp;
            String la = a.lessonId != null ? a.lessonId : "";
            String lb = b.lessonId != null ? b.lessonId : "";
            return la.compareTo(lb);
        });
        return days;
    }

    private List<LessonDayInfo> loadScheduledLessonDays() {
        List<LessonDayInfo> days = new ArrayList<>();
        try (Cursor c = dbHelper.findLessonDaysForGroupSubject(groupId, subjectId)) {
            while (c.moveToNext()) {
                String lessonId = c.getString(c.getColumnIndexOrThrow("lesson_id"));
                int dayOfWeek = c.getInt(c.getColumnIndexOrThrow("day_of_week"));
                days.add(new LessonDayInfo(dayOfWeek, lessonId));
            }
        }
        return days;
    }

    public List<String> getWeekDates(LocalDate weekStart, List<LessonDayInfo> lessonDays) {
        List<String> dates = new ArrayList<>();
        for (LessonDayInfo day : lessonDays) {
            LocalDate date = weekStart.with(DayOfWeek.of(day.dayOfWeek));
            dates.add(date.format(DateTimeFormatter.ofPattern("dd.MM")));
        }
        return dates;
    }

    public List<String> getWeekFullDates(LocalDate weekStart, List<LessonDayInfo> lessonDays) {
        List<String> dates = new ArrayList<>();
        for (LessonDayInfo day : lessonDays) {
            LocalDate date = weekStart.with(DayOfWeek.of(day.dayOfWeek));
            dates.add(date.toString());
        }
        return dates;
    }

    public List<String> getLessonIdsForWeek(List<LessonDayInfo> lessonDays) {
        List<String> ids = new ArrayList<>();
        for (LessonDayInfo day : lessonDays) {
            ids.add(day.lessonId);
        }
        return ids;
    }

    public List<JournalStudentItem> getStudentsForWeek(LocalDate weekStart, List<String> weekFullDates,
                                                        List<String> weekLessonIds,
                                                        LocalDate periodStart, LocalDate periodEnd) {
        List<String[]> studentList = new ArrayList<>();
        try (Cursor c = dbHelper.findStudentsInGroupRaw(groupId)) {
            while (c.moveToNext()) {
                String id   = c.getString(c.getColumnIndexOrThrow("student_id"));
                String name = c.getString(c.getColumnIndexOrThrow("student_name"));
                studentList.add(new String[]{id, name});
            }
        }

        int colCount = weekFullDates.size();
        Map<String, CellGrade[][]> cellsByStudent = new LinkedHashMap<>();
        for (String[] s : studentList) {
            CellGrade[][] cols = new CellGrade[colCount][];
            for (int i = 0; i < colCount; i++) {
                cols[i] = null;
            }
            cellsByStudent.put(s[0], cols);
        }

        String weekStartStr = weekStart.toString();
        String weekEndStr = weekStart.plusDays(7).toString();

        try (Cursor c = dbHelper.findGradesForGroupSubjectRaw(groupId, subjectId)) {
            while (c.moveToNext()) {
                String studentId = c.getString(c.getColumnIndexOrThrow("student_id"));
                String gradeId   = c.getString(c.getColumnIndexOrThrow("grade_id"));
                int    value     = c.getInt(c.getColumnIndexOrThrow("value"));
                String gradeType = c.getString(c.getColumnIndexOrThrow("grade_type"));
                String lessonId  = c.getString(c.getColumnIndexOrThrow("lesson_id"));
                String dateStr   = c.getString(c.getColumnIndexOrThrow("created_at"));

                if (!cellsByStudent.containsKey(studentId)) continue;

                String display = GradeDisplayHelper.formatJournalCell(value, gradeType);
                if (display.isEmpty()) continue;

                String gradeDate = extractDate(dateStr);
                if (gradeDate == null) continue;
                if (gradeDate.compareTo(weekStartStr) < 0 || gradeDate.compareTo(weekEndStr) >= 0) {
                    continue;
                }

                int col = resolveColumnIndex(lessonId, gradeDate, weekLessonIds, weekFullDates);
                if (col < 0) continue;

                putCellGrade(cellsByStudent.get(studentId), col,
                        new CellGrade(gradeId, display, dateStr != null ? dateStr : gradeDate));
            }
        }

        try (Cursor c = dbHelper.findAttendanceForGroupInPeriod(groupId, weekStartStr, weekEndStr)) {
            while (c.moveToNext()) {
                String studentId = c.getString(c.getColumnIndexOrThrow("student_id"));
                String lessonId  = c.getString(c.getColumnIndexOrThrow("lesson_id"));
                String attDate   = c.getString(c.getColumnIndexOrThrow("date"));
                String status    = c.getString(c.getColumnIndexOrThrow("status"));
                if (!cellsByStudent.containsKey(studentId) || lessonId == null) continue;

                String displayValue;
                if ("ABSENT".equals(status)) displayValue = "\u041D";
                else if ("EXCUSED_ABSENT".equals(status)) displayValue = "\u0411";
                else continue;

                int col = indexOfLesson(lessonId, weekLessonIds);
                if (col < 0 && attDate != null) {
                    col = resolveColumnIndex(lessonId, attDate, weekLessonIds, weekFullDates);
                }
                if (col < 0) continue;

                CellGrade[][] cols = cellsByStudent.get(studentId);
                if (cols[col] == null) {
                    cols[col] = new CellGrade[]{new CellGrade("att:" + lessonId, displayValue, "")};
                }
            }
        }

        Map<String, PeriodAvg> periodAverages = computePeriodAverages(periodStart, periodEnd);

        List<JournalStudentItem> items = new ArrayList<>();
        for (String[] s : studentList) {
            String id = s[0];
            String name = s[1];
            CellGrade[][] cols = cellsByStudent.get(id);
            String[] marksArr = new String[colCount];
            String[] gradeIdsArr = new String[colCount];

            for (int i = 0; i < colCount; i++) {
                if (cols[i] != null && cols[i].length > 0) {
                    marksArr[i] = joinDisplays(cols[i]);
                    gradeIdsArr[i] = cols[i][cols[i].length - 1].gradeId;
                } else {
                    marksArr[i] = "";
                    gradeIdsArr[i] = null;
                }
            }

            PeriodAvg pa = periodAverages.get(id);
            String avg = (pa != null && pa.count > 0)
                    ? String.format("%.1f", pa.sum / pa.count)
                    : "\u2014";
            items.add(new JournalStudentItem(id, name, avg, marksArr, gradeIdsArr));
        }

        if (items.isEmpty()) {
            items.add(new JournalStudentItem("", "\u041D\u0435\u0442 \u0441\u0442\u0443\u0434\u0435\u043D\u0442\u043E\u0432", "\u2014", new String[]{}, new String[]{}));
        }
        return items;
    }

    private static String extractDate(String dateStr) {
        if (dateStr == null || dateStr.length() < 10) return null;
        return dateStr.substring(0, 10);
    }

    private static int resolveColumnIndex(String lessonId, String gradeDate,
                                          List<String> weekLessonIds, List<String> weekFullDates) {
        String lid = lessonId != null ? lessonId.trim() : "";
        if (!lid.isEmpty()) {
            for (int i = 0; i < weekLessonIds.size(); i++) {
                if (lid.equals(weekLessonIds.get(i))
                        && i < weekFullDates.size()
                        && gradeDate.equals(weekFullDates.get(i))) {
                    return i;
                }
            }
            List<Integer> lessonCols = new ArrayList<>();
            for (int i = 0; i < weekLessonIds.size(); i++) {
                if (lid.equals(weekLessonIds.get(i))) {
                    lessonCols.add(i);
                }
            }
            if (lessonCols.size() == 1) {
                return lessonCols.get(0);
            }
            if (lessonCols.size() > 1) {
                for (int i : lessonCols) {
                    if (i < weekFullDates.size() && gradeDate.equals(weekFullDates.get(i))) {
                        return i;
                    }
                }
                return lessonCols.get(0);
            }
        }
        List<Integer> dateCols = new ArrayList<>();
        for (int i = 0; i < weekFullDates.size(); i++) {
            if (gradeDate.equals(weekFullDates.get(i))) {
                dateCols.add(i);
            }
        }
        if (dateCols.size() == 1) {
            return dateCols.get(0);
        }
        if (dateCols.size() > 1) {
            if (!lid.isEmpty()) {
                for (int i : dateCols) {
                    if (lid.equals(weekLessonIds.get(i))) {
                        return i;
                    }
                }
            }
            return dateCols.get(0);
        }
        return -1;
    }

    private static int indexOfLesson(String lessonId, List<String> weekLessonIds) {
        for (int i = 0; i < weekLessonIds.size(); i++) {
            if (lessonId.equals(weekLessonIds.get(i))) return i;
        }
        return -1;
    }

    private static void putCellGrade(CellGrade[][] cols, int col, CellGrade incoming) {
        if (cols[col] == null) {
            cols[col] = new CellGrade[]{incoming};
            return;
        }
        for (CellGrade existing : cols[col]) {
            if (existing.gradeId.equals(incoming.gradeId)) {
                if (incoming.sortKey.compareTo(existing.sortKey) >= 0) {
                    existing.display = incoming.display;
                    existing.sortKey = incoming.sortKey;
                }
                return;
            }
        }
        CellGrade[] merged = new CellGrade[cols[col].length + 1];
        System.arraycopy(cols[col], 0, merged, 0, cols[col].length);
        merged[cols[col].length] = incoming;
        cols[col] = merged;
    }

    private static String joinDisplays(CellGrade[] grades) {
        if (grades.length == 1) return grades[0].display;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < grades.length; i++) {
            if (i > 0) sb.append('\u00B7');
            sb.append(grades[i].display);
        }
        return sb.toString();
    }

    private Map<String, PeriodAvg> computePeriodAverages(LocalDate periodStart, LocalDate periodEnd) {
        Map<String, PeriodAvg> result = new LinkedHashMap<>();
        String startStr = periodStart.toString();
        String endStr = periodEnd.plusDays(1).toString();

        try (Cursor c = dbHelper.findGradesForGroupSubjectInPeriod(groupId, subjectId, startStr, endStr)) {
            while (c.moveToNext()) {
                String studentId = c.getString(c.getColumnIndexOrThrow("student_id"));
                int    value     = c.getInt(c.getColumnIndexOrThrow("value"));
                String gradeType = c.getString(c.getColumnIndexOrThrow("grade_type"));

                result.putIfAbsent(studentId, new PeriodAvg());

                if (GradeDisplayHelper.countsForAverage(value, gradeType)) {
                    result.get(studentId).sum += value;
                    result.get(studentId).count++;
                }
            }
        }
        return result;
    }

    private static class CellGrade {
        final String gradeId;
        String display;
        String sortKey;

        CellGrade(String gradeId, String display, String sortKey) {
            this.gradeId = gradeId;
            this.display = display;
            this.sortKey = sortKey != null ? sortKey : "";
        }
    }

    private static class PeriodAvg {
        double sum = 0;
        int count = 0;
    }

    public static class LessonDayInfo {
        public final int dayOfWeek;
        public final String lessonId;

        public LessonDayInfo(int dayOfWeek, String lessonId) {
            this.dayOfWeek = dayOfWeek;
            this.lessonId = lessonId;
        }
    }
}
