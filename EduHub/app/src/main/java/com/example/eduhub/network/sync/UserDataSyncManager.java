package com.example.eduhub.network.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.network.RetrofitClient;
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.network.util.ApiMapUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Response;

public class UserDataSyncManager {
    private static final String TAG = "UserDataSync";
    private static final Object SYNC_LOCK = new Object();

    private static final String SYNC_PREFS = "eduhub_sync_state";
    private static final String KEY_LAST_SYNCED_USER = "last_synced_user_id";

    private static final ExecutorService NET_POOL = Executors.newFixedThreadPool(6);
    private static final ExecutorService BG_POOL = Executors.newSingleThreadExecutor();

    private final Context context;
    private final UserSessionManager session;

    public interface SyncCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public UserDataSyncManager(Context context) {
        this.context = context;
        this.session = UserSessionManager.getInstance(context);
    }

    public void syncUserData(String userId, String role, SyncCallback callback) {
        new Thread(() -> {
            SyncUiNotifier.beginSession(context, SyncUiNotifier.Mode.BACKGROUND);
            synchronized (SYNC_LOCK) {
                DBHelper dbHelper = null;
                try {
                    dbHelper = DBHelper.getInstance(context);
                    String email = session.getEmail();

                    
                    if (isDifferentProfile(userId)) {
                        Log.d(TAG, "New profile detected, clearing local data");
                        dbHelper.clearAllData();
                    }

                    syncCritical(dbHelper, userId, role, email);
                    setLastSyncedUser(userId);

                    Log.d(TAG, "Critical sync completed for userId=" + userId);
                    if (callback != null) callback.onSuccess();
                } catch (Exception e) {
                    Log.e(TAG, "Critical sync failed", e);
                    
                    if (callback != null) callback.onFailure(e.getMessage());
                } finally {
                    if (dbHelper != null) dbHelper.close();
                }
            }

            
            startBackgroundSync(userId, role);
        }).start();
    }

    public boolean hasCachedProfile(String userId) {
        if (userId == null || !userId.equals(getLastSyncedUser())) {
            return false;
        }
        DBHelper db = DBHelper.getInstance(context);
        Cursor c = null;
        try {
            SQLiteDatabase sqlite = db.getReadableDatabase();
            c = sqlite.rawQuery("SELECT 1 FROM " + DBHelper.TABLE_USERS
                    + " WHERE id = ? LIMIT 1", new String[]{userId});
            return c.moveToFirst();
        } catch (Exception e) {
            Log.w(TAG, "hasCachedProfile failed", e);
            return false;
        } finally {
            if (c != null) c.close();
            db.close();
        }
    }

    public void refreshInBackground(String userId, String role) {
        new Thread(() -> {
            SyncUiNotifier.beginSession(context, SyncUiNotifier.Mode.BACKGROUND);
            synchronized (SYNC_LOCK) {
                DBHelper db = null;
                try {
                    db = DBHelper.getInstance(context);
                    String email = session.getEmail();
                    if (isDifferentProfile(userId)) {
                        db.clearAllData();
                    }
                    syncCritical(db, userId, role, email);
                    setLastSyncedUser(userId);
                    Log.d(TAG, "Background critical refresh completed for userId=" + userId);
                } catch (Exception e) {
                    Log.w(TAG, "Background critical refresh failed", e);
                } finally {
                    if (db != null) db.close();
                }
            }
            
            DataSyncEvents.notifyBackgroundComplete();
            
            startBackgroundSync(userId, role);
        }).start();
    }


    private boolean isDifferentProfile(String userId) {
        String last = getLastSyncedUser();
        return last == null || !last.equals(userId);
    }

    private String getLastSyncedUser() {
        return syncPrefs().getString(KEY_LAST_SYNCED_USER, null);
    }

    private void setLastSyncedUser(String userId) {
        syncPrefs().edit().putString(KEY_LAST_SYNCED_USER, userId).apply();
    }

    public void clearSyncState() {
        syncPrefs().edit().remove(KEY_LAST_SYNCED_USER).apply();
    }

    private SharedPreferences syncPrefs() {
        return context.getApplicationContext()
                .getSharedPreferences(SYNC_PREFS, Context.MODE_PRIVATE);
    }


    private void syncCritical(DBHelper db, String userId, String role, String email) {
        boolean isTeacher = role != null && role.contains("TEACHER");
        boolean isStudent = role != null && role.contains("STUDENT");

        
        runParallel(Arrays.asList(
                () -> syncUser(db, userId, email),
                () -> {
                    if (isTeacher) syncTeacherCritical(db, userId, email);
                    if (isStudent) syncStudentCritical(db, userId, email);
                }
        ));

        
        
        syncReferenceFromLessons(db);
    }

    private void syncReferenceFromLessons(DBHelper db) {
        Set<String> subjectIds = distinctLessonColumn(db, "subject_id");
        Set<String> teacherIds = distinctLessonColumn(db, "teacher_id");

        List<Runnable> tasks = new ArrayList<>(subjectIds.size() + teacherIds.size());
        for (String sid : subjectIds) {
            final String id = sid;
            tasks.add(() -> syncSubjectById(db, id));
        }
        for (String tid : teacherIds) {
            final String id = tid;
            tasks.add(() -> syncTeacherById(db, id));
        }
        runParallel(tasks);
    }

    private Set<String> distinctLessonColumn(DBHelper db, String column) {
        Set<String> ids = new LinkedHashSet<>();
        SQLiteDatabase sqlite = db.getReadableDatabase();
        Cursor c = null;
        try {
            c = sqlite.rawQuery("SELECT DISTINCT " + column + " FROM " + DBHelper.TABLE_LESSONS
                    + " WHERE " + column + " IS NOT NULL AND " + column + " <> ''", null);
            while (c.moveToNext()) {
                String v = c.getString(0);
                if (v != null && !v.isEmpty()) ids.add(v);
            }
        } catch (Exception e) {
            Log.w(TAG, "distinctLessonColumn failed: " + column, e);
        } finally {
            if (c != null) c.close();
        }
        return ids;
    }

    private void syncSubjectById(DBHelper db, String subjectId) {
        try {
            Map<String, Object> s = call(RetrofitClient.getInstance().getApiService()
                    .getSubjectById(parseApiId(subjectId)));
            if (s != null) saveSubject(db, s);
        } catch (Exception e) {
            Log.w(TAG, "syncSubjectById failed for " + subjectId, e);
        }
    }

    private void syncTeacherById(DBHelper db, String teacherId) {
        try {
            Map<String, Object> t = call(RetrofitClient.getInstance().getApiService()
                    .getTeacherById(parseApiId(teacherId)));
            if (t != null) saveTeacher(db, t);
        } catch (Exception e) {
            Log.w(TAG, "syncTeacherById failed for " + teacherId, e);
        }
    }


    private void startBackgroundSync(String userId, String role) {
        BG_POOL.execute(() -> {
            synchronized (SYNC_LOCK) {
                DBHelper db = null;
                try {
                    db = DBHelper.getInstance(context);
                    final DBHelper fdb = db;
                    String email = session.getEmail();
                    boolean isTeacher = role != null && role.contains("TEACHER");
                    boolean isStudent = role != null && role.contains("STUDENT");

                    
                    
                    runParallel(Arrays.asList(
                            () -> syncAllSubjects(fdb),
                            () -> syncAllTeachers(fdb)
                    ));

                    if (isTeacher) {
                        syncTeacherBackground(db, userId, email);
                    }
                    if (isStudent) {
                        syncStudentBackground(db, userId, email);
                    }
                    Log.d(TAG, "Background sync completed for userId=" + userId);
                } catch (Exception e) {
                    Log.w(TAG, "Background sync failed", e);
                } finally {
                    if (db != null) db.close();
                }
            }
            DataSyncEvents.notifyBackgroundComplete();
            SyncUiNotifier.endSession();
        });
    }

    private void recordPull(String key, int count) {
        SyncUiNotifier.recordDownload(key, count);
    }

    private void runParallel(List<Runnable> tasks) {
        List<Future<?>> futures = new ArrayList<>(tasks.size());
        for (Runnable task : tasks) {
            futures.add(NET_POOL.submit(task));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                Log.w(TAG, "Parallel sync task failed", e);
            }
        }
    }


    private void syncUser(DBHelper db, String userId, String email) {
        try {
            int uid = Integer.parseInt(userId);
            Map<String, Object> user = call(RetrofitClient.getInstance().getApiService().getUserById(uid));
            if (user != null) {
                saveUser(db, user);
                recordPull("user", 1);
                return;
            }
        } catch (Exception e) {
            Log.w(TAG, "API getUserById failed, trying local fallback", e);
        }
        loadUserLocally(db, userId, email);
    }

    private void loadUserLocally(DBHelper db, String userId, String email) {
        Cursor c = null;
        try {
            SQLiteDatabase sqlite = db.getReadableDatabase();
            if (email != null) {
                c = sqlite.rawQuery("SELECT * FROM " + DBHelper.TABLE_USERS + " WHERE email = ?", new String[]{email});
            } else {
                c = sqlite.rawQuery("SELECT * FROM " + DBHelper.TABLE_USERS + " WHERE id = ?", new String[]{userId});
            }
            if (c.moveToFirst()) {
                ContentValues cv = new ContentValues();
                for (String col : c.getColumnNames()) {
                    int idx = c.getColumnIndex(col);
                    int type = c.getType(idx);
                    if (type == Cursor.FIELD_TYPE_STRING) cv.put(col, c.getString(idx));
                    else if (type == Cursor.FIELD_TYPE_INTEGER) cv.put(col, c.getInt(idx));
                    else if (type == Cursor.FIELD_TYPE_FLOAT) cv.put(col, c.getFloat(idx));
                    else cv.put(col, c.getString(idx));
                }
                db.insertRow(DBHelper.TABLE_USERS, cv);
                Log.d(TAG, "Loaded user from local DB: " + c.getString(c.getColumnIndexOrThrow("id")));
            }
        } catch (Exception e) {
            Log.e(TAG, "loadUserLocally failed", e);
        } finally {
            if (c != null) c.close();
        }
    }


    private void syncTeacherCritical(DBHelper db, String userId, String email) {
        String localTeacherId = findLocalTeacherId(db, userId, email);
        String teacherId = session.getTeacherId();

        try {
            int tid = parseApiId(teacherId != null ? teacherId : localTeacherId != null ? localTeacherId : userId);
            Map<String, Object> teacher = call(RetrofitClient.getInstance().getApiService().getTeacherById(tid));
            if (teacher != null) {
                Map<String, Object> user = safeMap(teacher, "user");
                ContentValues cv = new ContentValues();
                cv.put("id", safeStr(teacher, "id"));
                cv.put("user_id", user != null ? safeStr(user, "id") : userId);
                cv.put("employee_id", safeStr(teacher, "employeeId"));
                cv.put("department", safeStr(teacher, "department"));
                db.insertRow(DBHelper.TABLE_TEACHERS, cv);
                localTeacherId = safeStr(teacher, "id");
                if (teacherId == null) {
                    teacherId = localTeacherId;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "API getTeacherById failed", e);
        }

        if (teacherId == null) {
            teacherId = localTeacherId;
        }
        if (teacherId == null) {
            Log.w(TAG, "No teacher record found for userId=" + userId);
            return;
        }

        List<String> groupIds = new ArrayList<>();
        try {
            int tid = parseApiId(teacherId);
            List<Map<String, Object>> groups = callList(RetrofitClient.getInstance().getApiService().getTeacherGroups(tid));
            if (groups != null) {
                recordPull("groups", groups.size());
                for (Map<String, Object> g : groups) {
                    try {
                        String gid = saveGroup(db, g);
                        if (gid != null) groupIds.add(gid);
                    } catch (Exception ex) {
                        Log.w(TAG, "saveGroup skipped: " + safeStr(g, "className"), ex);
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "API getTeacherGroups failed", e);
        }

        if (groupIds.isEmpty()) {
            groupIds = loadTeacherGroupsLocally(db, teacherId);
        }

        
        List<Runnable> lessonTasks = new ArrayList<>();
        for (String gid : groupIds) {
            final String g = gid;
            lessonTasks.add(() -> syncLessonsForGroup(db, g));
        }
        runParallel(lessonTasks);
        syncScheduleBreaks(db, new LinkedHashSet<>(groupIds));

        final String tIdFinal = teacherId;
        final String localTIdFinal = localTeacherId;
        try {
            int tid = parseApiId(tIdFinal);
            List<Map<String, Object>> subjects = callList(RetrofitClient.getInstance().getApiService().getTeacherSubjects(tid));
            if (subjects != null) {
                recordPull("subjects", subjects.size());
                for (Map<String, Object> s : subjects) saveSubject(db, s);
            }
        } catch (Exception e) {
            Log.w(TAG, "API getTeacherSubjects failed, loading local", e);
            if (localTIdFinal != null) loadSubjectsLocally(db, localTIdFinal);
        }
    }

    private void syncTeacherBackground(DBHelper db, String userId, String email) {
        String teacherId = session.getTeacherId();
        if (teacherId == null) {
            teacherId = findLocalTeacherId(db, userId, email);
        }
        if (teacherId == null) return;

        List<String> groupIds = loadTeacherGroupsLocally(db, ApiMapUtils.normalizeId(teacherId));

        List<Runnable> tasks = new ArrayList<>();
        for (String gid : groupIds) {
            final String g = gid;
            tasks.add(() -> {
                syncStudentsInGroup(db, g);
                syncAssignmentsForGroup(db, g);
                syncHomeworkCompletionsForGroup(db, g);
            });
        }
        runParallel(tasks);
    }

    private String findLocalTeacherId(DBHelper db, String userId, String email) {
        SQLiteDatabase sqlite = db.getReadableDatabase();
        Cursor c = null;
        try {
            String lookup = email != null ? email : userId;
            String emailQuery = email != null ? email : null;
            if (emailQuery != null) {
                c = sqlite.rawQuery(
                        "SELECT t.id FROM " + DBHelper.TABLE_TEACHERS + " t " +
                        "JOIN " + DBHelper.TABLE_USERS + " u ON u.id = t.user_id " +
                        "WHERE u.email = ?", new String[]{emailQuery});
                if (c.moveToFirst()) return c.getString(0);
            }
        } catch (Exception e) {
            Log.w(TAG, "findLocalTeacherId failed", e);
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    private List<String> loadTeacherGroupsLocally(DBHelper db, String localTeacherId) {
        List<String> ids = new ArrayList<>();
        SQLiteDatabase sqlite = db.getReadableDatabase();
        Cursor c = null;
        try {
            c = sqlite.rawQuery("SELECT DISTINCT g.id, g.* FROM " + DBHelper.TABLE_GROUPS + " g " +
                    "JOIN " + DBHelper.TABLE_LESSONS + " l ON l.group_id = g.id " +
                    "WHERE l.teacher_id = ?", new String[]{localTeacherId});
            while (c.moveToNext()) {
                ContentValues cv = new ContentValues();
                for (String col : c.getColumnNames()) {
                    int idx = c.getColumnIndex(col);
                    int type = c.getType(idx);
                    if (type == Cursor.FIELD_TYPE_STRING) cv.put(col, c.getString(idx));
                    else if (type == Cursor.FIELD_TYPE_INTEGER) cv.put(col, c.getInt(idx));
                    else if (type == Cursor.FIELD_TYPE_FLOAT) cv.put(col, c.getFloat(idx));
                }
                db.insertRow(DBHelper.TABLE_GROUPS, cv);
                ids.add(ApiMapUtils.normalizeId(c.getString(c.getColumnIndexOrThrow("id"))));
            }
        } catch (Exception e) {
            Log.e(TAG, "loadTeacherGroupsLocally failed", e);
        } finally {
            if (c != null) c.close();
        }
        return ids;
    }

    private void loadSubjectsLocally(DBHelper db, String localTeacherId) {
        SQLiteDatabase sqlite = db.getReadableDatabase();
        Cursor c = null;
        try {
            c = sqlite.rawQuery("SELECT DISTINCT sub.* FROM " + DBHelper.TABLE_SUBJECTS + " sub " +
                    "JOIN " + DBHelper.TABLE_LESSONS + " l ON l.subject_id = sub.id " +
                    "WHERE l.teacher_id = ?", new String[]{localTeacherId});
            while (c.moveToNext()) {
                ContentValues cv = new ContentValues();
                for (String col : c.getColumnNames()) {
                    int idx = c.getColumnIndex(col);
                    int type = c.getType(idx);
                    if (type == Cursor.FIELD_TYPE_STRING) cv.put(col, c.getString(idx));
                    else if (type == Cursor.FIELD_TYPE_INTEGER) cv.put(col, c.getInt(idx));
                    else if (type == Cursor.FIELD_TYPE_FLOAT) cv.put(col, c.getFloat(idx));
                }
                db.insertRow(DBHelper.TABLE_SUBJECTS, cv);
            }
        } catch (Exception e) {
            Log.e(TAG, "loadSubjectsLocally failed", e);
        } finally {
            if (c != null) c.close();
        }
    }


    private void syncStudentCritical(DBHelper db, String userId, String email) {
        String localStudentId = findLocalStudentId(db, userId, email);
        String studentId = session.getStudentId();
        String groupId = session.getGroupId();

        try {
            int sid = parseApiId(studentId != null ? studentId : localStudentId != null ? localStudentId : userId);
            Map<String, Object> student = call(RetrofitClient.getInstance().getApiService().getStudentById(sid));
            if (student != null) {
                Map<String, Object> user = safeMap(student, "user");
                ContentValues cv = new ContentValues();
                studentId = ApiMapUtils.normalizeId(safeStr(student, "id"));
                cv.put("id", studentId);
                cv.put("user_id", user != null ? ApiMapUtils.normalizeId(safeStr(user, "id")) : userId);
                cv.put("group_id", ApiMapUtils.normalizeId(safeStr(student, "groupId")));
                cv.put("enrollment_date", safeStr(student, "enrollmentDate"));
                cv.put("student_id_number", safeStr(student, "studentIdNumber"));
                db.insertRow(DBHelper.TABLE_STUDENTS, cv);
                recordPull("user", 1);
                if (groupId == null) {
                    groupId = ApiMapUtils.normalizeId(safeStr(student, "groupId"));
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "API getStudentById failed", e);
        }

        if (studentId == null) {
            studentId = localStudentId;
        }
        if (studentId != null) {
            studentId = ApiMapUtils.normalizeId(studentId);
        }

        if (groupId == null && studentId != null) {
            groupId = readStudentGroupLocally(db, studentId);
        }

        if (groupId != null) {
            groupId = ApiMapUtils.normalizeId(groupId);
            try {
                int gid = parseApiId(groupId);
                Map<String, Object> group = call(RetrofitClient.getInstance().getApiService().getGroupById(gid));
                if (group != null) {
                    saveGroup(db, group);
                    recordPull("groups", 1);
                }
            } catch (Exception e) {
                Log.w(TAG, "API getGroupById failed, using local", e);
            }
            syncLessonsForGroup(db, groupId);
            syncScheduleBreaks(db, new LinkedHashSet<>(Arrays.asList(groupId)));
        }

        
        if (studentId != null) {
            try {
                int sid = parseApiId(studentId);
                syncGradesForStudent(db, sid);
                syncAttendanceForStudent(db, sid);
            } catch (Exception e) {
                Log.w(TAG, "Early student marks sync failed", e);
            }
        }
    }

    private void syncStudentBackground(DBHelper db, String userId, String email) {
        String studentId = session.getStudentId();
        if (studentId == null) {
            studentId = findLocalStudentId(db, userId, email);
        }
        if (studentId != null) {
            studentId = ApiMapUtils.normalizeId(studentId);
        }

        String groupId = session.getGroupId();
        if (groupId == null && studentId != null) {
            groupId = readStudentGroupLocally(db, studentId);
        }

        List<Runnable> tasks = new ArrayList<>();
        if (groupId != null) {
            final String g = ApiMapUtils.normalizeId(groupId);
            tasks.add(() -> syncStudentsInGroup(db, g));
            tasks.add(() -> syncAssignmentsForGroup(db, g));
        }
        if (studentId != null) {
            final int sid = parseApiId(studentId);
            tasks.add(() -> syncGradesForStudent(db, sid));
            tasks.add(() -> syncAttendanceForStudent(db, sid));
            tasks.add(() -> syncHomeworkCompletionsForStudent(db, sid));
        }
        tasks.add(() -> syncNotificationsForUser(db, userId));
        runParallel(tasks);
    }

    private String readStudentGroupLocally(DBHelper db, String studentId) {
        SQLiteDatabase sqlite = db.getReadableDatabase();
        Cursor c = null;
        try {
            c = sqlite.rawQuery("SELECT group_id FROM " + DBHelper.TABLE_STUDENTS + " WHERE id = ?", new String[]{studentId});
            if (c.moveToFirst()) return ApiMapUtils.normalizeId(c.getString(0));
        } catch (Exception ex) {
            Log.w(TAG, "readStudentGroupLocally failed", ex);
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    private String findLocalStudentId(DBHelper db, String userId, String email) {
        SQLiteDatabase sqlite = db.getReadableDatabase();
        Cursor c = null;
        try {
            if (email != null) {
                c = sqlite.rawQuery(
                        "SELECT s.id FROM " + DBHelper.TABLE_STUDENTS + " s " +
                        "JOIN " + DBHelper.TABLE_USERS + " u ON u.id = s.user_id " +
                        "WHERE u.email = ?", new String[]{email});
                if (c.moveToFirst()) return c.getString(0);
            }
        } catch (Exception e) {
            Log.w(TAG, "findLocalStudentId failed", e);
        } finally {
            if (c != null) c.close();
        }
        return null;
    }


    private void syncAllSubjects(DBHelper db) {
        try {
            List<Map<String, Object>> subjects = callList(
                    RetrofitClient.getInstance().getApiService().getAllSubjects());
            if (subjects != null) {
                recordPull("subjects", subjects.size());
                for (Map<String, Object> s : subjects) saveSubject(db, s);
            }
        } catch (Exception e) {
            Log.w(TAG, "syncAllSubjects failed", e);
        }
    }

    private void syncAllTeachers(DBHelper db) {
        try {
            List<Map<String, Object>> teachers = callList(
                    RetrofitClient.getInstance().getApiService().getAllTeachers());
            if (teachers != null) {
                recordPull("teachers", teachers.size());
                for (Map<String, Object> t : teachers) saveTeacher(db, t);
            }
        } catch (Exception e) {
            Log.w(TAG, "syncAllTeachers failed", e);
        }
    }

    private void saveTeacher(DBHelper db, Map<String, Object> teacher) {
        Map<String, Object> user = safeMap(teacher, "user");
        if (user != null) saveUser(db, user);
        ContentValues cv = new ContentValues();
        cv.put("id", safeStr(teacher, "id"));
        cv.put("user_id", user != null ? safeStr(user, "id") : safeStr(teacher, "userId"));
        cv.put("employee_id", safeStr(teacher, "employeeId"));
        cv.put("department", safeStr(teacher, "department"));
        db.insertRow(DBHelper.TABLE_TEACHERS, cv);
    }

    private void syncScheduleBreaks(DBHelper db, Set<String> groupIds) {
        try {
            List<Map<String, Object>> breaks = callList(
                    RetrofitClient.getInstance().getApiService().getAllScheduleBreaks());
            if (breaks == null) return;
            int saved = 0;
            for (Map<String, Object> item : breaks) {
                String breakGroupId = ApiMapUtils.normalizeId(safeStr(item, "groupId"));
                if (breakGroupId == null) continue;
                if (groupIds != null && !groupIds.isEmpty() && !groupIds.contains(breakGroupId)) continue;
                ContentValues cv = new ContentValues();
                cv.put("id", safeStr(item, "id"));
                cv.put("group_id", breakGroupId);
                cv.put("day_of_week", safeInt(item, "dayOfWeek") != null ? safeInt(item, "dayOfWeek") : 1);
                cv.put("start_time", safeStr(item, "startTime"));
                cv.put("end_time", safeStr(item, "endTime"));
                cv.put("label", breakLabelForTime(safeStr(item, "startTime")));
                db.insertRow(DBHelper.TABLE_BREAKS, cv);
                saved++;
            }
            if (saved > 0) recordPull("breaks", saved);
        } catch (Exception e) {
            Log.w(TAG, "syncScheduleBreaks failed", e);
        }
    }

    private void syncAssignmentsForGroup(DBHelper db, String groupId) {
        groupId = ApiMapUtils.normalizeId(groupId);
        try {
            Map<String, String> filters = new HashMap<>();
            filters.put("groupId", groupId);
            List<Map<String, Object>> assignments = callList(
                    RetrofitClient.getInstance().getApiService().getAssignments(filters));
            if (assignments == null) return;
            recordPull("assignments", assignments.size());
            for (Map<String, Object> item : assignments) saveAssignment(db, item);
        } catch (Exception e) {
            Log.w(TAG, "syncAssignmentsForGroup failed", e);
        }
    }

    private void syncHomeworkCompletionsForGroup(DBHelper db, String groupId) {
        groupId = ApiMapUtils.normalizeId(groupId);
        SQLiteDatabase sqlite = db.getReadableDatabase();
        Cursor c = null;
        try {
            c = sqlite.rawQuery(
                    "SELECT id FROM " + DBHelper.TABLE_ASSIGNMENTS + " WHERE group_id = ?",
                    new String[]{groupId});
            while (c.moveToNext()) {
                syncHomeworkCompletionsForAssignment(db, c.getString(0));
            }
        } catch (Exception e) {
            Log.w(TAG, "syncHomeworkCompletionsForGroup failed", e);
        } finally {
            if (c != null) c.close();
        }
    }

    private void syncHomeworkCompletionsForAssignment(DBHelper db, String assignmentId) {
        if (assignmentId == null || assignmentId.isEmpty()) return;
        try {
            Map<String, String> filters = new HashMap<>();
            filters.put("assignmentId", ApiMapUtils.normalizeId(assignmentId));
            List<Map<String, Object>> completions = callList(
                    RetrofitClient.getInstance().getApiService().getHomeworkCompletions(filters));
            if (completions == null) return;
            recordPull("homework", completions.size());
            for (Map<String, Object> item : completions) saveHomeworkCompletion(db, item);
        } catch (Exception e) {
            Log.w(TAG, "syncHomeworkCompletionsForAssignment failed for " + assignmentId, e);
        }
    }

    private void syncNotificationsForUser(DBHelper db, String userId) {
        try {
            Map<String, String> filters = new HashMap<>();
            filters.put("userId", userId);
            List<Map<String, Object>> notifications = callList(
                    RetrofitClient.getInstance().getApiService().getNotifications(filters));
            if (notifications == null) return;
            recordPull("notifications", notifications.size());
            for (Map<String, Object> item : notifications) saveNotification(db, item);
        } catch (Exception e) {
            Log.w(TAG, "syncNotificationsForUser failed", e);
        }
    }

    private String breakLabelForTime(String startTime) {
        if (startTime != null && startTime.startsWith("12:10")) {
            return "Большая перемена";
        }
        return "Перемена";
    }

    private void saveAssignment(DBHelper db, Map<String, Object> item) {
        ContentValues cv = new ContentValues();
        cv.put("id", safeStr(item, "id"));
        cv.put("title", safeStr(item, "title"));
        cv.put("description", safeStr(item, "description"));
        cv.put("subject_id", ApiMapUtils.normalizeId(safeStr(item, "subjectId")));
        cv.put("teacher_id", ApiMapUtils.normalizeId(safeStr(item, "teacherId")));
        cv.put("group_id", ApiMapUtils.normalizeId(safeStr(item, "groupId")));
        cv.put("assigned_date", safeStr(item, "assignedDate"));
        cv.put("due_date", safeStr(item, "dueDate"));
        cv.put("assignment_type", safeStr(item, "assignmentType") != null ? safeStr(item, "assignmentType") : "HOMEWORK");
        cv.put("max_points", safeInt(item, "maxPoints") != null ? safeInt(item, "maxPoints") : 10);
        db.insertRow(DBHelper.TABLE_ASSIGNMENTS, cv);
    }

    private void saveNotification(DBHelper db, Map<String, Object> item) {
        String type = safeStr(item, "notificationType");
        if ("HOMEWORK".equals(type)) type = "NEW_HOMEWORK";
        ContentValues cv = new ContentValues();
        cv.put("id", safeStr(item, "id"));
        cv.put("user_id", safeStr(item, "userId"));
        cv.put("title", safeStr(item, "title"));
        cv.put("message", safeStr(item, "details") != null ? safeStr(item, "details") : safeStr(item, "message"));
        cv.put("notification_type", type != null ? type : "SYSTEM");
        cv.put("is_read", safeInt(item, "isRead") != null && safeInt(item, "isRead") == 1 ? 1 : 0);
        cv.put("created_at", safeStr(item, "time") != null ? safeStr(item, "time") : safeStr(item, "createdAt"));
        db.insertRow(DBHelper.TABLE_NOTIFICATIONS, cv);
    }


    private void syncStudentsInGroup(DBHelper db, String groupId) {
        groupId = ApiMapUtils.normalizeId(groupId);
        try {
            List<Map<String, Object>> students = callList(RetrofitClient.getInstance().getApiService().getAllStudents(groupId));
            if (students != null) {
                recordPull("students", students.size());
                for (Map<String, Object> s : students) {
                    Map<String, Object> user = safeMap(s, "user");
                    if (user != null) saveUser(db, user);
                    ContentValues sv = new ContentValues();
                    sv.put("id", safeStr(s, "id"));
                    sv.put("user_id", user != null ? safeStr(user, "id") : safeStr(s, "id"));
                    sv.put("group_id", groupId);
                    sv.put("enrollment_date", safeStr(s, "enrollmentDate"));
                    sv.put("student_id_number", safeStr(s, "studentIdNumber"));
                    db.insertRow(DBHelper.TABLE_STUDENTS, sv);
                }
                return;
            }
        } catch (Exception e) {
            Log.w(TAG, "API syncStudentsInGroup failed, loading local", e);
        }
        loadStudentsFromLocal(db, groupId);
    }

    private void loadStudentsFromLocal(DBHelper db, String groupId) {
        SQLiteDatabase sqlite = db.getReadableDatabase();
        Cursor c = null;
        try {
            c = sqlite.rawQuery("SELECT * FROM " + DBHelper.TABLE_STUDENTS + " WHERE group_id = ?", new String[]{groupId});
            while (c.moveToNext()) {
                ContentValues cv = new ContentValues();
                for (String col : c.getColumnNames()) {
                    int idx = c.getColumnIndex(col);
                    int type = c.getType(idx);
                    if (type == Cursor.FIELD_TYPE_STRING) cv.put(col, c.getString(idx));
                    else if (type == Cursor.FIELD_TYPE_INTEGER) cv.put(col, c.getInt(idx));
                    else if (type == Cursor.FIELD_TYPE_FLOAT) cv.put(col, c.getFloat(idx));
                }
                db.insertRow(DBHelper.TABLE_STUDENTS, cv);
            }
        } catch (Exception e) {
            Log.e(TAG, "loadStudentsFromLocal failed", e);
        } finally {
            if (c != null) c.close();
        }
    }

    private void syncLessonsForGroup(DBHelper db, String groupId) {
        groupId = ApiMapUtils.normalizeId(groupId);
        try {
            Map<String, String> filters = new HashMap<>();
            filters.put("groupId", groupId);
            List<Map<String, Object>> lessons = callList(RetrofitClient.getInstance().getApiService().getLessons(filters));
            if (lessons != null) {
                recordPull("lessons", lessons.size());
                for (Map<String, Object> item : lessons) saveLesson(db, item);
                return;
            }
        } catch (Exception e) {
            Log.w(TAG, "API syncLessonsForGroup failed, loading local", e);
        }
        loadLessonsFromLocal(db, groupId);
    }

    private void loadLessonsFromLocal(DBHelper db, String groupId) {
        SQLiteDatabase sqlite = db.getReadableDatabase();
        Cursor c = null;
        try {
            c = sqlite.rawQuery("SELECT * FROM " + DBHelper.TABLE_LESSONS + " WHERE group_id = ?", new String[]{groupId});
            while (c.moveToNext()) {
                ContentValues cv = new ContentValues();
                for (String col : c.getColumnNames()) {
                    int idx = c.getColumnIndex(col);
                    int type = c.getType(idx);
                    if (type == Cursor.FIELD_TYPE_STRING) cv.put(col, c.getString(idx));
                    else if (type == Cursor.FIELD_TYPE_INTEGER) cv.put(col, c.getInt(idx));
                    else if (type == Cursor.FIELD_TYPE_FLOAT) cv.put(col, c.getFloat(idx));
                }
                db.insertRow(DBHelper.TABLE_LESSONS, cv);
            }
        } catch (Exception e) {
            Log.e(TAG, "loadLessonsFromLocal failed", e);
        } finally {
            if (c != null) c.close();
        }
    }

    private void syncGradesForStudent(DBHelper db, int studentId) {
        try {
            Map<String, String> filters = new HashMap<>();
            filters.put("studentId", String.valueOf(studentId));
            List<Map<String, Object>> grades = callList(RetrofitClient.getInstance().getApiService().getGrades(filters));
            if (grades != null) {
                recordPull("grades", grades.size());
                for (Map<String, Object> item : grades) saveGrade(db, item);
                Log.d(TAG, "Synced " + grades.size() + " grades for student " + studentId);
            }
        } catch (Exception e) {
            Log.w(TAG, "syncGradesForStudent failed", e);
        }
    }

    private void syncAttendanceForStudent(DBHelper db, int studentId) {
        try {
            Map<String, String> filters = new HashMap<>();
            filters.put("studentId", String.valueOf(studentId));
            List<Map<String, Object>> records = callList(
                    RetrofitClient.getInstance().getApiService().getAttendance(filters));
            if (records == null) return;
            recordPull("attendance", records.size());
            for (Map<String, Object> item : records) saveAttendance(db, item);
            Log.d(TAG, "Synced " + records.size() + " attendance records for student " + studentId);
        } catch (Exception e) {
            Log.w(TAG, "syncAttendanceForStudent failed", e);
        }
    }

    private void syncHomeworkCompletionsForStudent(DBHelper db, int studentId) {
        try {
            Map<String, String> filters = new HashMap<>();
            filters.put("studentId", String.valueOf(studentId));
            List<Map<String, Object>> completions = callList(
                    RetrofitClient.getInstance().getApiService().getHomeworkCompletions(filters));
            if (completions == null) return;
            recordPull("homework", completions.size());
            for (Map<String, Object> item : completions) saveHomeworkCompletion(db, item);
            Log.d(TAG, "Synced " + completions.size() + " homework completions for student " + studentId);
        } catch (Exception e) {
            Log.w(TAG, "syncHomeworkCompletionsForStudent failed", e);
        }
    }


    private void saveUser(DBHelper db, Map<String, Object> user) {
        ContentValues cv = new ContentValues();
        cv.put("id", safeStr(user, "id"));
        cv.put("email", safeStr(user, "email"));
        cv.put("first_name", safeStr(user, "firstName"));
        cv.put("last_name", safeStr(user, "lastName"));
        cv.put("patronymic", safeStr(user, "patronymic"));
        cv.put("phone_number", safeStr(user, "phoneNumber"));
        cv.put("role", safeStr(user, "role"));
        cv.put("is_active", safeInt(user, "isActive") != null ? safeInt(user, "isActive") : 1);
        cv.put("avatar_url", safeStr(user, "avatarUrl"));
        db.upsertUserFromSync(cv);
    }

    private String saveGroup(DBHelper db, Map<String, Object> g) {
        ContentValues gv = new ContentValues();
        gv.put("id", safeStr(g, "id"));
        gv.put("code", safeStr(g, "code"));
        gv.put("name", safeStr(g, "className") != null ? safeStr(g, "className") : safeStr(g, "name"));
        gv.put("course_number", DBHelper.normalizeGroupCourseNumber(safeInt(g, "courseNumber")));
        gv.put("specialization", safeStr(g, "specialization"));
        gv.put("max_students", safeInt(g, "maxStudents") != null ? safeInt(g, "maxStudents") : 30);
        db.insertRow(DBHelper.TABLE_GROUPS, gv);
        return ApiMapUtils.normalizeId(safeStr(g, "id"));
    }

    private void saveSubject(DBHelper db, Map<String, Object> s) {
        ContentValues sv = new ContentValues();
        sv.put("id", safeStr(s, "id"));
        sv.put("code", safeStr(s, "code"));
        sv.put("name", safeStr(s, "subjectName") != null ? safeStr(s, "subjectName") : safeStr(s, "name"));
        sv.put("description", safeStr(s, "description"));
        sv.put("total_hours", safeInt(s, "totalHours") != null ? safeInt(s, "totalHours") : 0);
        sv.put("credits", safeInt(s, "credits") != null ? safeInt(s, "credits") : 0);
        sv.put("is_exam", safeInt(s, "isExam") != null ? safeInt(s, "isExam") : 0);
        sv.put("is_credit", safeInt(s, "isCredit") != null ? safeInt(s, "isCredit") : 0);
        db.insertRow(DBHelper.TABLE_SUBJECTS, sv);
    }

    private void saveLesson(DBHelper db, Map<String, Object> item) {
        ContentValues cv = new ContentValues();
        cv.put("id", safeStr(item, "lessonId") != null ? safeStr(item, "lessonId") : safeStr(item, "id"));
        cv.put("group_id", ApiMapUtils.normalizeId(safeStr(item, "groupId")));
        cv.put("subject_id", ApiMapUtils.normalizeId(safeStr(item, "subjectId")));
        cv.put("teacher_id", ApiMapUtils.normalizeId(safeStr(item, "teacherId")));
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

    private void saveGrade(DBHelper db, Map<String, Object> item) {
        ContentValues cv = new ContentValues();
        cv.put("id", safeStr(item, "gradeId") != null ? safeStr(item, "gradeId") : safeStr(item, "id"));
        cv.put("student_id", ApiMapUtils.normalizeId(safeStr(item, "studentId")));
        cv.put("subject_id", ApiMapUtils.normalizeId(safeStr(item, "subjectId")));
        cv.put("teacher_id", ApiMapUtils.normalizeId(safeStr(item, "teacherId")));
        cv.put("value", safeInt(item, "value") != null ? safeInt(item, "value") : 0);
        cv.put("grade_type", safeStr(item, "gradeType"));
        cv.put("comment", safeStr(item, "comment"));
        cv.put("lesson_id", ApiMapUtils.normalizeId(safeStr(item, "lessonId")));
        cv.put("weight", safeInt(item, "weight") != null ? safeInt(item, "weight") : 1);
        cv.put("created_at", safeStr(item, "createdAt"));
        db.insertRow(DBHelper.TABLE_GRADES, cv);
    }

    private void saveAttendance(DBHelper db, Map<String, Object> item) {
        ContentValues cv = new ContentValues();
        cv.put("id", ApiMapUtils.normalizeId(safeStr(item, "id")));
        cv.put("student_id", ApiMapUtils.normalizeId(safeStr(item, "studentId")));
        cv.put("lesson_id", ApiMapUtils.normalizeId(safeStr(item, "lessonId")));
        cv.put("date", safeStr(item, "date"));
        cv.put("status", safeStr(item, "status"));
        cv.put("comment", safeStr(item, "comment"));
        cv.put("marked_at", safeStr(item, "markedAt"));
        cv.put("marked_by", ApiMapUtils.normalizeId(safeStr(item, "markedBy")));
        db.insertRow(DBHelper.TABLE_ATTENDANCE, cv);
    }

    private void saveHomeworkCompletion(DBHelper db, Map<String, Object> item) {
        ContentValues cv = new ContentValues();
        cv.put("id", ApiMapUtils.normalizeId(
                safeStr(item, "completionId") != null ? safeStr(item, "completionId") : safeStr(item, "id")));
        cv.put("assignment_id", ApiMapUtils.normalizeId(safeStr(item, "assignmentId")));
        cv.put("student_id", ApiMapUtils.normalizeId(safeStr(item, "studentId")));
        cv.put("status", mapCompletionStatus(safeStr(item, "status")));
        cv.put("student_comment", safeStr(item, "studentComment"));
        cv.put("submitted_at", safeStr(item, "submittedAt"));
        cv.put("received_points", safeInt(item, "receivedPoints"));
        cv.put("graded_at", safeStr(item, "gradedAt"));
        db.insertRow(DBHelper.TABLE_COMPLETIONS, cv);
    }

    private static String mapCompletionStatus(String apiStatus) {
        if (apiStatus == null) return "NOT_STARTED";
        switch (apiStatus) {
            case "ASSIGNED":
                return "NOT_STARTED";
            case "ON_REVIEW":
                return "SUBMITTED";
            case "GRADED":
                return "COMPLETED";
            default:
                return apiStatus;
        }
    }


    private <T> T call(Call<T> call) {
        try {
            Response<T> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            Log.w(TAG, "API call failed: " + response.code());
        } catch (Exception e) {
            Log.w(TAG, "API call error: " + e.getMessage());
        }
        return null;
    }

    private List<Map<String, Object>> callList(Call<List<Map<String, Object>>> call) {
        try {
            Response<List<Map<String, Object>>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            Log.w(TAG, "API list call failed: " + response.code());
        } catch (Exception e) {
            Log.w(TAG, "API list call error: " + e.getMessage());
        }
        return null;
    }

    private int parseApiId(String id) {
        return Integer.parseInt(ApiMapUtils.normalizeId(id));
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
}
