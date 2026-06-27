package com.example.eduhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.network.ApiConfig;
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.security.AuthManager;
import com.example.eduhub.teacher.TeacherMainActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    @Inject
    AuthManager authManager;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_scrin);

        TextInputEditText etEmail = findViewById(R.id.et_email);
        TextInputEditText etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.login_progress);
        TextView tvApiServer = findViewById(R.id.tv_api_server);
        tvApiServer.setText("Сервер: " + ApiConfig.BASE_URL);

        showLoading(true);

        authManager.restoreExistingSession(
            () -> runOnUiThread(() -> navigateAfterLogin(authManager.getUserId(), authManager.getRole())),
            () -> runOnUiThread(() -> {
                showLoading(false);
                btnLogin.setOnClickListener(v -> {
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    showLoading(true);
                    authManager.login(email, password, new AuthManager.AuthCallback() {
                        @Override
                        public void onSuccess(String userId, String role) {
                            runOnUiThread(() -> {
                                navigateAfterLogin(userId, role);
                            });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            runOnUiThread(() -> {
                                showLoading(false);
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                });
            })
        );
    }

    private void showLoading(boolean loading) {
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
        if (btnLogin != null) {
            btnLogin.setEnabled(!loading);
        }
    }

    private boolean navigateAfterLogin(String userId, String role) {
        showLoading(false);
        String upperRole = role != null ? role.toUpperCase(Locale.ROOT) : "";
        boolean isTeacher = upperRole.contains("TEACHER");
        boolean isStudent = upperRole.contains("STUDENT");

        Intent intent;
        if (isTeacher && isStudent) {
            intent = new Intent(this, RoleSelectionActivity.class);
            intent.putExtra("userId", userId);
        } else if (isTeacher) {
            intent = new Intent(this, TeacherMainActivity.class);
            intent.putExtra("userId", userId);
        } else if (isStudent) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("userId", userId);
        } else {
            UserSessionManager.getInstance(this).clearSession();
            Toast.makeText(this, "Неизвестная роль пользователя", Toast.LENGTH_SHORT).show();
            if (btnLogin != null) {
                showLoading(false);
            }
            return false;
        }
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBHelper dbHelper = DBHelper.getInstance(this);
        if (dbHelper != null) dbHelper.close();
    }
}
