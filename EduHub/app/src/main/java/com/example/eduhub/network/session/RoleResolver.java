package com.example.eduhub.network.session;

import androidx.annotation.Nullable;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.domains.classes.Student;
import com.example.eduhub.domains.classes.Teacher;

import java.util.Locale;

public final class RoleResolver {

    private RoleResolver() {
    }

    public static boolean isNavigableRole(@Nullable String role) {
        if (role == null || role.isEmpty()) {
            return false;
        }
        String upper = role.toUpperCase(Locale.ROOT);
        return upper.contains("TEACHER") || upper.contains("STUDENT");
    }

    @Nullable
    public static String resolve(DBHelper dbHelper, String userId,
                                 @Nullable String rawRole,
                                 @Nullable Integer studentId,
                                 @Nullable Integer teacherId) {
        String role = normalize(rawRole);
        if (isNavigableRole(role)) {
            return role;
        }

        boolean hasStudent = studentId != null && studentId > 0;
        boolean hasTeacher = teacherId != null && teacherId > 0;

        if (!hasStudent || !hasTeacher) {
            Student student = dbHelper.findStudentByUserId(userId);
            if (student != null) {
                hasStudent = true;
            }
            Teacher teacher = dbHelper.findTeacherByUserId(userId);
            if (teacher != null) {
                hasTeacher = true;
            }
        }

        if (hasStudent && hasTeacher) {
            return "TEACHER,STUDENT";
        }
        if (hasTeacher) {
            return "TEACHER";
        }
        if (hasStudent) {
            return "STUDENT";
        }

        String fromUsers = dbHelper.findUserRoleByUserId(userId);
        role = normalize(fromUsers);
        return isNavigableRole(role) ? role : null;
    }

    @Nullable
    private static String normalize(@Nullable String role) {
        if (role == null) {
            return null;
        }
        String trimmed = role.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toUpperCase(Locale.ROOT);
    }
}
