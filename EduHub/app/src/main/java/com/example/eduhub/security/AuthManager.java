package com.example.eduhub.security;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.domains.classes.Student;
import com.example.eduhub.domains.classes.Teacher;
import com.example.eduhub.domains.classes.User;
import com.example.eduhub.network.RetrofitClient;
import com.example.eduhub.network.dto.AuthRequest;
import com.example.eduhub.network.dto.AuthResponse;
import com.example.eduhub.network.session.RoleResolver;
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.network.sync.UserDataSyncManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AuthManager {

    private final Context context;
    private final DBHelper dbHelper;
    private final UserSessionManager sessionManager;

    @Inject
    public AuthManager(@ApplicationContext Context context, DBHelper dbHelper, UserSessionManager sessionManager) {
        this.context = context;
        this.dbHelper = dbHelper;
        this.sessionManager = sessionManager;
    }

    public interface AuthCallback {
        void onSuccess(String userId, String role);
        void onFailure(String errorMessage);
    }

    public void login(String email, String password, AuthCallback callback) {
        if (isNetworkAvailable()) {
            loginViaApi(email, password, callback);
        } else {
            loginLocally(email, password, true, callback);
        }
    }

    public boolean restoreExistingSession() {
        if (!sessionManager.isLoggedIn()) {
            return false;
        }
        String savedToken = sessionManager.getToken();
        String savedRefreshToken = sessionManager.getRefreshToken();

        if (savedToken == null && savedRefreshToken == null) {
            sessionManager.clearSession();
            return false;
        }

        RetrofitClient.setTokens(savedToken, savedRefreshToken);

        if (!isNetworkAvailable()) {
            String userId = sessionManager.getUserId();
            String role = RoleResolver.resolve(dbHelper, userId, sessionManager.getRole(),
                    parsePositiveInt(sessionManager.getStudentId()),
                    parsePositiveInt(sessionManager.getTeacherId()));
            if (RoleResolver.isNavigableRole(role)) {
                return true;
            }
            sessionManager.clearSession();
            RetrofitClient.clearTokens();
            return false;
        }

        try {
            Response<AuthResponse> response = RetrofitClient.getInstance()
                    .getApiService()
                    .getAuthMe()
                    .execute();

            if (response.isSuccessful() && response.body() != null) {
                AuthResponse auth = response.body();
                String userId = String.valueOf(auth.getUserId());
                String role = RoleResolver.resolve(dbHelper, userId, auth.getRole(),
                        auth.getStudentId(), auth.getTeacherId());

                if (RoleResolver.isNavigableRole(role)) {
                    String studentId = auth.getStudentId() != null ? String.valueOf(auth.getStudentId()) : null;
                    String teacherId = auth.getTeacherId() != null ? String.valueOf(auth.getTeacherId()) : null;
                    String groupId = auth.getGroupId() != null ? String.valueOf(auth.getGroupId()) : null;

                    sessionManager.saveSession(userId, role,
                            auth.getFirstName(), auth.getLastName(), auth.getPatronymic(),
                            sessionManager.getEmail(), groupId, auth.getGroupName(),
                            studentId, teacherId);

                    UserDataSyncManager sync = new UserDataSyncManager(context);
                    sync.refreshInBackground(userId, role);
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        sessionManager.clearSession();
        RetrofitClient.clearTokens();
        return false;
    }

    public String getUserId() {
        return sessionManager.getUserId();
    }

    public String getRole() {
        return sessionManager.getRole();
    }

    public void logout() {
        String refreshTok = sessionManager.getRefreshToken();
        if (refreshTok != null && isNetworkAvailable()) {
            try {
                java.util.Map<String, String> body = new java.util.HashMap<>();
                body.put("refreshToken", refreshTok);
                RetrofitClient.getInstance().getApiService().logout(body).execute();
            } catch (Exception ignored) {
            }
        }
        sessionManager.clearSession();
        RetrofitClient.clearTokens();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    private void loginViaApi(String email, String password, AuthCallback callback) {
        AuthRequest request = new AuthRequest(email, password);
        RetrofitClient.getInstance().getApiService().login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();

                    if (auth.getToken() != null) {
                        RetrofitClient.setTokens(auth.getToken(), auth.getRefreshToken());
                        sessionManager.saveToken(auth.getToken());
                        sessionManager.saveRefreshToken(auth.getRefreshToken());
                    }

                    completeApiLogin(auth, email, password, callback);
                } else {
                    callback.onFailure("Неверный логин или пароль");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                if (loginLocally(email, password, false, callback)) {
                    return;
                }
                callback.onFailure("Ошибка подключения к серверу");
            }
        });
    }

    private void completeApiLogin(AuthResponse auth, String email, String password, AuthCallback callback) {
        String userId = String.valueOf(auth.getUserId());
        String role = RoleResolver.resolve(dbHelper, userId, auth.getRole(),
                auth.getStudentId(), auth.getTeacherId());

        if (!RoleResolver.isNavigableRole(role)) {
            sessionManager.clearSession();
            RetrofitClient.clearTokens();
            callback.onFailure("Неверный логин или пароль");
            return;
        }

        String studentId = auth.getStudentId() != null ? String.valueOf(auth.getStudentId()) : null;
        String teacherId = auth.getTeacherId() != null ? String.valueOf(auth.getTeacherId()) : null;
        String groupId = auth.getGroupId() != null ? String.valueOf(auth.getGroupId()) : null;

        sessionManager.saveSession(
                userId, role,
                auth.getFirstName(), auth.getLastName(), auth.getPatronymic(),
                email, groupId, auth.getGroupName(),
                studentId, teacherId);

        saveUserLocally(auth, email, password, role);

        UserDataSyncManager sync = new UserDataSyncManager(context);
        if (sync.hasCachedProfile(userId)) {
            sync.refreshInBackground(userId, role);
            callback.onSuccess(userId, role);
            return;
        }

        sync.syncUserData(userId, role, new UserDataSyncManager.SyncCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess(userId, role);
            }

            @Override
            public void onFailure(String error) {
                callback.onSuccess(userId, role);
            }
        });
    }

    private void saveUserLocally(AuthResponse auth, String email, String password, String resolvedRole) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String userId = String.valueOf(auth.getUserId());
        db.execSQL("INSERT OR REPLACE INTO " + DBHelper.TABLE_USERS +
                " (id, email, password_hash, first_name, last_name, patronymic, role, is_active, avatar_url) VALUES (?, ?, ?, ?, ?, ?, ?, 1, 'default_avatar')",
                new Object[]{
                        userId,
                        email != null ? email : "",
                        PasswordHasher.hash(password),
                        auth.getFirstName() != null ? auth.getFirstName() : "",
                        auth.getLastName() != null ? auth.getLastName() : "",
                        auth.getPatronymic() != null ? auth.getPatronymic() : "",
                        resolvedRole != null ? resolvedRole : ""
                });
    }

    private boolean loginLocally(String email, String password, boolean showErrorOnFail, AuthCallback callback) {
        User user = dbHelper.authenticateUser(email, password);

        if (user == null) {
            if (showErrorOnFail) {
                callback.onFailure("Неверный логин или пароль");
            }
            return false;
        }

        String userId = user.getId().toString();
        LocalIds localIds = resolveLocalIds(userId);
        String role = RoleResolver.resolve(dbHelper, userId, dbHelper.findUserRoleByUserId(userId),
                parsePositiveInt(localIds.studentId), parsePositiveInt(localIds.teacherId));

        if (!RoleResolver.isNavigableRole(role)) {
            if (showErrorOnFail) {
                callback.onFailure("Неверный логин или пароль");
            }
            return false;
        }

        sessionManager.saveSession(
                userId, role,
                user.getFirstName(), user.getLastName(), user.getPatronymic(),
                email, localIds.groupId, null,
                localIds.studentId, localIds.teacherId);

        callback.onSuccess(userId, role);
        return true;
    }

    private LocalIds resolveLocalIds(String userId) {
        LocalIds ids = new LocalIds();
        Student student = dbHelper.findStudentByUserId(userId);
        if (student != null && student.getId() != null) {
            ids.studentId = student.getId().toString();
            if (student.getGroup() != null && student.getGroup().getId() != null) {
                ids.groupId = student.getGroup().getId().toString();
            }
        }
        Teacher teacher = dbHelper.findTeacherByUserId(userId);
        if (teacher != null && teacher.getId() != null) {
            ids.teacherId = teacher.getId().toString();
        }
        return ids;
    }

    private static Integer parsePositiveInt(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static final class LocalIds {
        String studentId;
        String teacherId;
        String groupId;
    }
}
