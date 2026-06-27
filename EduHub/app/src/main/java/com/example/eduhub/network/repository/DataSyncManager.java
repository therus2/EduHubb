package com.example.eduhub.network.repository;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.network.RetrofitClient;
import com.example.eduhub.network.util.ApiMapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class DataSyncManager {
    private static final String TAG = "DataSyncManager";
    private final Context context;

    public DataSyncManager(Context context) {
        this.context = context;
    }

    public interface SyncCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public void syncAll(final SyncCallback callback) {
        new Thread(() -> {
            try {
                DBHelper dbHelper = DBHelper.getInstance(context);

                syncUsers(dbHelper);
                syncGroups(dbHelper);
                syncSubjects(dbHelper);
                syncTeachers(dbHelper);
                syncStudents(dbHelper);
                syncLessons(dbHelper);
                syncGrades(dbHelper);
                syncAttendance(dbHelper);
                syncAssignments(dbHelper);
                syncHomeworkCompletions(dbHelper);
                syncAnnouncements(dbHelper);
                syncNotifications(dbHelper);
                syncScheduleBreaks(dbHelper);
                syncUserSettings(dbHelper);

                dbHelper.close();
                Log.d(TAG, "Sync completed successfully");
                if (callback != null) callback.onSuccess();
            } catch (Exception e) {
                Log.e(TAG, "Sync failed", e);
                if (callback != null) callback.onFailure(e.getMessage());
            }
        }).start();
    }

    private <T> T execute(Call<T> call) throws Exception {
        Response<T> response = call.execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        }
        Log.w(TAG, "API call failed: " + response.code() + " " + response.message());
        return null;
    }

    private String safeStr(Map<String, Object> map, String key) {
        return ApiMapUtils.safeStr(map, key);
    }

    private Integer safeInt(Map<String, Object> map, String key) {
        return ApiMapUtils.safeInt(map, key);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> safeMap(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object val = map.get(key);
        if (val instanceof Map) return (Map<String, Object>) val;
        return null;
    }

    private Call<List<Map<String, Object>>> callWithEmptyFilters(Call<List<Map<String, Object>>> call) {
        return call;
    }

    

    private void syncUsers(DBHelper db) throws Exception {
        List<Map<String, Object>> list = execute(RetrofitClient.getInstance().getApiService().getAllUsers());
        if (list == null) return;
        for (Map<String, Object> item : list) {
            ContentValues cv = new ContentValues();
            cv.put("id", safeStr(item, "id"));
            cv.put("email", safeStr(item, "email"));
            cv.put("first_name", safeStr(item, "firstName"));
            cv.put("last_name", safeStr(item, "lastName"));
            cv.put("patronymic", safeStr(item, "patronymic"));
            cv.put("phone_number", safeStr(item, "phoneNumber"));
            cv.put("role", safeStr(item, "role"));
            cv.put("is_active", safeInt(item, "isActive") != null ? safeInt(item, "isActive") : 1);
            cv.put("avatar_url", safeStr(item, "avatarUrl"));
            db.upsertUserFromSync(cv);
        }
        Log.d(TAG, "Synced " + list.size() + " users");
    }

    

    private void syncGroups(DBHelper db) throws Exception {
        List<Map<String, Object>> list = execute(RetrofitClient.getInstance().getApiService().getAllGroups());
        if (list == null) return;
        for (Map<String, Object> item : list) {
            ContentValues cv = new ContentValues();
            cv.put("id", safeStr(item, "id"));
            cv.put("code", safeStr(item, "code"));
            cv.put("name", safeStr(item, "className") != null ? safeStr(item, "className") : safeStr(item, "name"));
            cv.put("course_number", DBHelper.normalizeGroupCourseNumber(safeInt(item, "courseNumber")));
            cv.put("specialization", safeStr(item, "specialization"));
            cv.put("max_students", safeInt(item, "maxStudents") != null ? safeInt(item, "maxStudents") : safeInt(item, "studentsCount"));
            db.insertRow(DBHelper.TABLE_GROUPS, cv);
        }
        Log.d(TAG, "Synced " + list.size() + " groups");
    }

    

    private void syncSubjects(DBHelper db) throws Exception {
        List<Map<String, Object>> list = execute(RetrofitClient.getInstance().getApiService().getAllSubjects());
        if (list == null) return;
        for (Map<String, Object> item : list) {
            ContentValues cv = new ContentValues();
            cv.put("id", safeStr(item, "id"));
            cv.put("code", safeStr(item, "code"));
            cv.put("name", safeStr(item, "name"));
            cv.put("description", safeStr(item, "description"));
            cv.put("total_hours", safeInt(item, "totalHours") != null ? safeInt(item, "totalHours") : 0);
            cv.put("credits", safeInt(item, "credits") != null ? safeInt(item, "credits") : 0);
            cv.put("is_exam", safeInt(item, "isExam") != null ? safeInt(item, "isExam") : 0);
            cv.put("is_credit", safeInt(item, "isCredit") != null ? safeInt(item, "isCredit") : 0);
            db.insertRow(DBHelper.TABLE_SUBJECTS, cv);
        }
        Log.d(TAG, "Synced " + list.size() + " subjects");
    }

    

    private void syncTeachers(DBHelper db) throws Exception {
        List<Map<String, Object>> list = execute(RetrofitClient.getInstance().getApiService().getAllTeachers());
        if (list == null) return;
        for (Map<String, Object> item : list) {
            Map<String, Object> user = safeMap(item, "user");
            ContentValues cv = new ContentValues();
            cv.put("id", safeStr(item, "id"));
            cv.put("user_id", user != null ? safeStr(user, "id") : safeStr(item, "id"));
            cv.put("employee_id", safeStr(item, "employeeId"));
            cv.put("department", safeStr(item, "department"));
            db.insertRow(DBHelper.TABLE_TEACHERS, cv);
        }
        Log.d(TAG, "Synced " + list.size() + " teachers");
    }

    

    private void syncStudents(DBHelper db) throws Exception {
        List<Map<String, Object>> list = execute(RetrofitClient.getInstance().getApiService().getAllStudents(null));
        if (list == null) return;
        for (Map<String, Object> item : list) {
            Map<String, Object> user = safeMap(item, "user");
            ContentValues cv = new ContentValues();
            cv.put("id", safeStr(item, "id"));
            cv.put("user_id", user != null ? safeStr(user, "id") : safeStr(item, "id"));
            cv.put("group_id", safeStr(item, "groupId"));
            cv.put("enrollment_date", safeStr(item, "enrollmentDate"));
            cv.put("student_id_number", safeStr(item, "studentIdNumber"));
            db.insertRow(DBHelper.TABLE_STUDENTS, cv);
        }
        Log.d(TAG, "Synced " + list.size() + " students");
    }

    

    private void syncLessons(DBHelper db) throws Exception {
        List<Map<String, Object>> list = execute(RetrofitClient.getInstance().getApiService().getLessons(new HashMap<>()));
        if (list == null) return;
        for (Map<String, Object> item : list) {
            ContentValues cv = new ContentValues();
            cv.put("id", safeStr(item, "lessonId") != null ? safeStr(item, "lessonId") : safeStr(item, "id"));
            cv.put("group_id", safeStr(item, "groupId"));
            cv.put("subject_id", safeStr(item, "subjectId"));
            cv.put("teacher_id", safeStr(item, "teacherId"));
            Integer dow = safeInt(item, "dayOfWeek");
            cv.put("day_of_week", dow != null ? dow : 1);
            cv.put("start_time", safeStr(item, "startTime"));
            cv.put("end_time", safeStr(item, "endTime"));
            cv.put("classroom", safeStr(item, "classroom"));
            cv.put("lesson_type", safeStr(item, "lessonType"));
            cv.put("week_number", safeInt(item, "weekNumber") != null ? safeInt(item, "weekNumber") : 1);
            cv.put("is_alternating_week", safeInt(item, "isAlternatingWeek") != null ? safeInt(item, "isAlternatingWeek") : 0);
            cv.put("lesson_topic", safeStr(item, "lessonTopic"));
            db.insertRow(DBHelper.TABLE_LESSONS, cv);
        }
        Log.d(TAG, "Synced " + list.size() + " lessons");
    }

    

    private void syncGrades(DBHelper db) throws Exception {
        List<Map<String, Object>> list = execute(RetrofitClient.getInstance().getApiService().getGrades(new HashMap<>()));
        if (list == null) return;
        for (Map<String, Object> item : list) {
            ContentValues cv = new ContentValues();
            cv.put("id", safeStr(item, "gradeId") != null ? safeStr(item, "gradeId") : safeStr(item, "id"));
            cv.put("student_id", safeStr(item, "studentId"));
            cv.put("subject_id", safeStr(item, "subjectId"));
            cv.put("teacher_id", safeStr(item, "teacherId"));
            cv.put("value", safeInt(item, "value") != null ? safeInt(item, "value") : 0);
            cv.put("grade_type", safeStr(item, "gradeType"));
            cv.put("comment", safeStr(item, "comment"));
            cv.put("lesson_id", safeStr(item, "lessonId"));
            cv.put("weight", safeInt(item, "weight") != null ? safeInt(item, "weight") : 1);
            cv.put("created_at", safeStr(item, "createdAt"));
            db.insertRow(DBHelper.TABLE_GRADES, cv);
        }
        Log.d(TAG, "Synced " + list.size() + " grades");
    }

    

    private void syncAttendance(DBHelper db) throws Exception {
        List<Map<String, Object>> list = execute(RetrofitClient.getInstance().getApiService().getAttendance(new HashMap<>()));
        if (list == null) return;
        for (Map<String, Object> item : list) {
            ContentValues cv = new ContentValues();
            cv.put("id", safeStr(item, "id"));
            cv.put("student_id", safeStr(item, "studentId"));
            cv.put("lesson_id", safeStr(item, "lessonId"));
            cv.put("date", safeStr(item, "date"));
            cv.put("status", safeStr(item, "status"));
            cv.put("comment", safeStr(item, "comment"));
            cv.put("marked_at", safeStr(item, "markedAt"));
            cv.put("marked_by", safeStr(item, "markedBy"));
            db.insertRow(DBHelper.TABLE_ATTENDANCE, cv);
        }
        Log.d(TAG, "Synced " + list.size() + " attendance records");
    }

    

    private void syncAssignments(DBHelper db) throws Exception {
        List<Map<String, Object>> list = execute(RetrofitClient.getInstance().getApiService().getAssignments(new HashMap<>()));
        if (list == null) return;
        for (Map<String, Object> item : list) {
            ContentValues cv = new ContentValues();
            cv.put("id", safeStr(item, "id"));
            cv.put("title", safeStr(item, "title"));
            cv.put("description", safeStr(item, "description"));
            cv.put("subject_id", safeStr(item, "subjectId"));
            cv.put("teacher_id", safeStr(item, "teacherId"));
            cv.put("group_id", safeStr(item, "groupId"));
            cv.put("assigned_date", safeStr(item, "assignedDate"));
            cv.put("due_date", safeStr(item, "dueDate"));
            cv.put("assignment_type", safeStr(item, "assignmentType"));
            cv.put("attachments", safeStr(item, "attachments"));
            cv.put("max_points", safeInt(item, "maxPoints") != null ? safeInt(item, "maxPoints") : 0);
            db.insertRow(DBHelper.TABLE_ASSIGNMENTS, cv);
        }
        Log.d(TAG, "Synced " + list.size() + " assignments");
    }

    

    private void syncHomeworkCompletions(DBHelper db) throws Exception {
        List<Map<String, Object>> list = execute(RetrofitClient.getInstance().getApiService().getHomeworkCompletions(new HashMap<>()));
        if (list == null) return;
        for (Map<String, Object> item : list) {
            ContentValues cv = new ContentValues();
            cv.put("id", safeStr(item, "completionId") != null ? safeStr(item, "completionId") : safeStr(item, "id"));
            cv.put("assignment_id", safeStr(item, "assignmentId"));
            cv.put("student_id", safeStr(item, "studentId"));
            cv.put("status", safeStr(item, "status"));
            cv.put("student_comment", safeStr(item, "studentComment"));
            cv.put("submitted_at", safeStr(item, "submittedAt"));
            cv.put("received_points", safeInt(item, "receivedPoints"));
            cv.put("graded_at", safeStr(item, "gradedAt"));
            db.insertRow(DBHelper.TABLE_COMPLETIONS, cv);
        }
        Log.d(TAG, "Synced " + list.size() + " homework completions");
    }

    

    private void syncAnnouncements(DBHelper db) throws Exception {
        List<Map<String, Object>> list = execute(RetrofitClient.getInstance().getApiService().getAnnouncements(new HashMap<>()));
        if (list == null) return;
        for (Map<String, Object> item : list) {
            ContentValues cv = new ContentValues();
            cv.put("id", safeStr(item, "id"));
            cv.put("title", safeStr(item, "title"));
            cv.put("content", safeStr(item, "fullText") != null ? safeStr(item, "fullText") : safeStr(item, "content"));
            cv.put("author_id", safeStr(item, "authorId"));
            cv.put("target_group_id", safeStr(item, "targetGroupId"));
            cv.put("priority", safeStr(item, "priority"));
            cv.put("created_at", safeStr(item, "time") != null ? safeStr(item, "time") : safeStr(item, "createdAt"));
            cv.put("published_at", safeStr(item, "publishedAt"));
            cv.put("expires_at", safeStr(item, "expiresAt"));
            cv.put("is_published", safeInt(item, "isPublished") != null ? safeInt(item, "isPublished") : 1);
            cv.put("is_pinned", safeInt(item, "isPinned") != null ? safeInt(item, "isPinned") : 0);
            db.insertRow(DBHelper.TABLE_ANNOUNCEMENTS, cv);
        }
        Log.d(TAG, "Synced " + list.size() + " announcements");
    }

    

    private void syncNotifications(DBHelper db) throws Exception {
        List<Map<String, Object>> list = execute(RetrofitClient.getInstance().getApiService().getNotifications(new HashMap<>()));
        if (list == null) return;
        for (Map<String, Object> item : list) {
            ContentValues cv = new ContentValues();
            cv.put("id", safeStr(item, "id"));
            cv.put("user_id", safeStr(item, "userId"));
            cv.put("title", safeStr(item, "title"));
            cv.put("message", safeStr(item, "details") != null ? safeStr(item, "details") : safeStr(item, "message"));
            cv.put("notification_type", safeStr(item, "notificationType"));
            cv.put("is_read", safeInt(item, "isRead") != null ? safeInt(item, "isRead") : 0);
            cv.put("created_at", safeStr(item, "time") != null ? safeStr(item, "time") : safeStr(item, "createdAt"));
            cv.put("read_at", safeStr(item, "readAt"));
            cv.put("action_url", safeStr(item, "actionUrl"));
            db.insertRow(DBHelper.TABLE_NOTIFICATIONS, cv);
        }
        Log.d(TAG, "Synced " + list.size() + " notifications");
    }

    

    private void syncScheduleBreaks(DBHelper db) throws Exception {
        List<Map<String, Object>> list = execute(RetrofitClient.getInstance().getApiService().getAllScheduleBreaks());
        if (list == null) return;
        for (Map<String, Object> item : list) {
            ContentValues cv = new ContentValues();
            cv.put("id", safeStr(item, "id"));
            cv.put("group_id", safeStr(item, "groupId"));
            cv.put("day_of_week", safeInt(item, "dayOfWeek") != null ? safeInt(item, "dayOfWeek") : 0);
            cv.put("start_time", safeStr(item, "startTime"));
            cv.put("end_time", safeStr(item, "endTime"));
            cv.put("label", safeStr(item, "label"));
            db.insertRow(DBHelper.TABLE_BREAKS, cv);
        }
        Log.d(TAG, "Synced " + list.size() + " schedule breaks");
    }

    

    private void syncUserSettings(DBHelper db) throws Exception {
        List<Map<String, Object>> list = execute(RetrofitClient.getInstance().getApiService().getAllUserSettings());
        if (list == null) return;
        for (Map<String, Object> item : list) {
            ContentValues cv = new ContentValues();
            cv.put("user_id", safeStr(item, "userId"));
            Integer ng = safeInt(item, "notifyNewGrades");
            cv.put("notify_new_grades", ng != null ? ng : 1);
            Integer ns = safeInt(item, "notifyScheduleChanges");
            cv.put("notify_schedule_changes", ns != null ? ns : 1);
            Integer nh = safeInt(item, "notifyHomework");
            cv.put("notify_homework", nh != null ? nh : 1);
            Integer na = safeInt(item, "notifyAnnouncements");
            cv.put("notify_announcements", na != null ? na : 1);
            cv.put("theme", safeStr(item, "theme"));
            cv.put("language", safeStr(item, "language"));
            db.insertRow(DBHelper.TABLE_USER_SETTINGS, cv);
        }
        Log.d(TAG, "Synced " + list.size() + " user settings");
    }
}
