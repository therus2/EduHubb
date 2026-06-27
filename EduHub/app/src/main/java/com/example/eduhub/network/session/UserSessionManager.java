package com.example.eduhub.network.session;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {
    private static final String PREF_NAME = "eduhub_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "role";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_PATRONYMIC = "patronymic";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_GROUP_ID = "group_id";
    private static final String KEY_GROUP_NAME = "group_name";
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_TEACHER_ID = "teacher_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_REFRESH_TOKEN = "jwt_refresh_token";

    private static UserSessionManager instance;
    private final SharedPreferences prefs;

    private UserSessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized UserSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserSessionManager(context);
        }
        return instance;
    }

    public void saveSession(String userId, String role, String firstName,
                            String lastName, String patronymic, String email,
                            String groupId, String groupName,
                            String studentId, String teacherId) {
        prefs.edit()
                .putString(KEY_USER_ID, userId)
                .putString(KEY_ROLE, role)
                .putString(KEY_FIRST_NAME, firstName)
                .putString(KEY_LAST_NAME, lastName)
                .putString(KEY_PATRONYMIC, patronymic)
                .putString(KEY_EMAIL, email)
                .putString(KEY_GROUP_ID, groupId)
                .putString(KEY_GROUP_NAME, groupName)
                .putString(KEY_STUDENT_ID, studentId)
                .putString(KEY_TEACHER_ID, teacherId)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public void saveRefreshToken(String token) {
        prefs.edit().putString(KEY_REFRESH_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        if (!prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            return false;
        }
        String userId = getUserId();
        return userId != null && !userId.isEmpty();
    }

    public String getUserId() { return prefs.getString(KEY_USER_ID, null); }
    public String getRole() { return prefs.getString(KEY_ROLE, null); }
    public String getFirstName() { return prefs.getString(KEY_FIRST_NAME, null); }
    public String getLastName() { return prefs.getString(KEY_LAST_NAME, null); }
    public String getPatronymic() { return prefs.getString(KEY_PATRONYMIC, null); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, null); }
    public String getGroupId() { return prefs.getString(KEY_GROUP_ID, null); }
    public String getGroupName() { return prefs.getString(KEY_GROUP_NAME, null); }
    public String getStudentId() { return prefs.getString(KEY_STUDENT_ID, null); }
    public String getTeacherId() { return prefs.getString(KEY_TEACHER_ID, null); }

    public boolean isTeacher() {
        String role = getRole();
        return role != null && role.contains("TEACHER");
    }

    public boolean isStudent() {
        String role = getRole();
        return role != null && role.contains("STUDENT");
    }

    public String getFullName() {
        String ln = getLastName();
        String fn = getFirstName();
        String p = getPatronymic();
        StringBuilder sb = new StringBuilder();
        if (ln != null) sb.append(ln).append(" ");
        if (fn != null) sb.append(fn).append(" ");
        if (p != null) sb.append(p);
        return sb.toString().trim();
    }
}
