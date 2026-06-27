package com.example.eduhub;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.teacher.TeacherMainActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RoleSelectionActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        userId = getIntent().getStringExtra("userId");
        if (userId == null || userId.isEmpty()) {
            userId = com.example.eduhub.network.session.UserSessionManager
                    .getInstance(this).getUserId();
        }
        if (userId == null || userId.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        dbHelper = DBHelper.getInstance(this);

        Button btnTeacher = findViewById(R.id.btn_role_teacher);
        Button btnStudent = findViewById(R.id.btn_role_student);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT role FROM users WHERE id = ?", new String[]{userId});

        boolean isTeacher = false;
        boolean isStudent = false;
        if (cursor.moveToFirst()) {
            String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
            isTeacher = role.contains("TEACHER");
            isStudent = role.contains("STUDENT");
        }
        cursor.close();

        if (isTeacher && isStudent) {
            btnTeacher.setOnClickListener(v -> {
                Intent intent = new Intent(this, TeacherMainActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            });
            btnStudent.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            });
        } else if (isTeacher) {
            Intent intent = new Intent(this, TeacherMainActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        } else if (isStudent) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.close();
    }
}
