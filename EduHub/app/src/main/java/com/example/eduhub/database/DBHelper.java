package com.example.eduhub.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.eduhub.domains.classes.Grade;
import com.example.eduhub.domains.classes.Attendance;
import com.example.eduhub.domains.classes.Assignment;
import com.example.eduhub.domains.classes.HomeworkCompletion;
import com.example.eduhub.domains.classes.Lesson;
import com.example.eduhub.domains.classes.Notification;
import com.example.eduhub.domains.classes.Announcement;
import com.example.eduhub.domains.classes.Student;
import com.example.eduhub.domains.classes.Teacher;
import com.example.eduhub.domains.classes.Subject;
import com.example.eduhub.domains.classes.StudentGroup;
import com.example.eduhub.domains.classes.User;
import com.example.eduhub.database.schema.DatabaseCreator;
import com.example.eduhub.security.PasswordHasher;
import com.example.eduhub.network.sync.ServerSyncManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DBHelper extends SQLiteOpenHelper {

    
    
    private static final String DATABASE_NAME = "college_diary.db";
    private static final int DATABASE_VERSION = 21;
    private static final String APP_PREFERENCES = "diary_prefs";

    private static volatile DBHelper instance;

    private final Context context;
    private SharedPreferences sharedPreferences;

    
    public static DBHelper getInstance(Context context) {
        if (context == null) return null;
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    
    public static String normalizeGradeTypeForStorage(String raw) {
        if (raw == null || raw.isEmpty()) return "CURRENT";
        String t = raw.trim().toUpperCase();
        switch (t) {
            case "CURRENT":
            case "CONTROL":
            case "TEST":
            case "PRACTICAL":
            case "EXAM":
            case "CREDIT":
            case "HOMEWORK":
            case "ESSAY":
            case "TEST_PREP":
            case "LAB":
            case "ABSENT":
            case "EXEMPT":
            case "CREDIT_PASS":
                return t;
            case "REPORT":
            case "PROJECT":
                return "ESSAY";
            default:
                Log.w("DBHelper", "Unknown grade_type \"" + raw + "\", using CURRENT");
                return "CURRENT";
        }
    }

    

    
    public void reassignAssignmentId(String localId, String serverId) {
        if (localId == null || serverId == null) return;
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues a = new ContentValues();
            a.put(COLUMN_ASSIGNMENT_ID, serverId);
            db.update(TABLE_ASSIGNMENTS, a, COLUMN_ASSIGNMENT_ID + " = ?", new String[]{localId});

            ContentValues c = new ContentValues();
            c.put(COLUMN_COMPLETION_ASSIGNMENT_ID, serverId);
            db.update(TABLE_COMPLETIONS, c, COLUMN_COMPLETION_ASSIGNMENT_ID + " = ?", new String[]{localId});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void reassignGradeId(String localId, String serverId) {
        if (localId == null || serverId == null) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_GRADE_ID, serverId);
        db.update(TABLE_GRADES, cv, COLUMN_GRADE_ID + " = ?", new String[]{localId});
    }

    public void reassignAttendanceId(String localId, String serverId) {
        if (localId == null || serverId == null) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ATTENDANCE_ID, serverId);
        db.update(TABLE_ATTENDANCE, cv, COLUMN_ATTENDANCE_ID + " = ?", new String[]{localId});
    }

    public void reassignCompletionId(String localId, String serverId) {
        if (localId == null || serverId == null) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_COMPLETION_ID, serverId);
        db.update(TABLE_COMPLETIONS, cv, COLUMN_COMPLETION_ID + " = ?", new String[]{localId});
    }

    

    
    public void enqueueSyncOp(String entity, String op, String localId,
                              String serverId, String payloadJson, String dependsOn) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SQ_ENTITY, entity);
        cv.put(SQ_OP, op);
        cv.put(SQ_LOCAL_ID, localId);
        cv.put(SQ_SERVER_ID, serverId);
        cv.put(SQ_PAYLOAD, payloadJson);
        cv.put(SQ_DEPENDS_ON, dependsOn);
        cv.put(SQ_ATTEMPTS, 0);
        cv.put(SQ_NEXT_ATTEMPT_AT, 0);
        cv.put(SQ_CREATED_AT, System.currentTimeMillis());
        db.insert(TABLE_SYNC_QUEUE, null, cv);
    }

    
    public void putIdMap(String entity, String localId, String serverId) {
        if (entity == null || localId == null || serverId == null) return;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(IDM_ENTITY, entity);
        cv.put(IDM_LOCAL_ID, localId);
        cv.put(IDM_SERVER_ID, serverId);
        db.insertWithOnConflict(TABLE_ID_MAP, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    
    public String getMappedServerId(String entity, String localId) {
        if (entity == null || localId == null) return null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(TABLE_ID_MAP, new String[]{IDM_SERVER_ID},
                    IDM_ENTITY + " = ? AND " + IDM_LOCAL_ID + " = ?",
                    new String[]{entity, localId}, null, null, null);
            if (c.moveToFirst()) return c.getString(0);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    
    
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD_HASH = "password_hash";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_PATRONYMIC = "patronymic";
    public static final String COLUMN_PHONE = "phone_number";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_LAST_LOGIN = "last_login_at";
    public static final String COLUMN_IS_ACTIVE = "is_active";
    public static final String COLUMN_AVATAR = "avatar_url";

    
    
    public static final String TABLE_STUDENTS = "students";
    public static final String COLUMN_STUDENT_ID = "id";
    public static final String COLUMN_USER_ID_FK = "user_id";
    public static final String COLUMN_GROUP_ID = "id";
    public static final String COLUMN_STUDENT_GROUP_ID = "group_id";
    public static final String COLUMN_ENROLLMENT_DATE = "enrollment_date";
    public static final String COLUMN_STUDENT_NUMBER = "student_id_number";

    
    
    public static final String TABLE_TEACHERS = "teachers";
    public static final String COLUMN_TEACHER_ID = "id";
    public static final String COLUMN_EMPLOYEE_ID = "employee_id";
    public static final String COLUMN_DEPARTMENT = "department";

    
    
    public static final String TABLE_GROUPS = "student_groups";
    public static final String COLUMN_GROUP_CODE = "code";
    public static final String COLUMN_GROUP_NAME = "name";
    public static final String COLUMN_COURSE_NUMBER = "course_number";
    public static final String COLUMN_SPECIALIZATION = "specialization";
    public static final String COLUMN_MAX_STUDENTS = "max_students";

    
    
    public static final String TABLE_SUBJECTS = "subjects";
    public static final String COLUMN_SUBJECT_ID = "id";
    public static final String COLUMN_SUBJECT_CODE = "code";
    public static final String COLUMN_SUBJECT_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_TOTAL_HOURS = "total_hours";
    public static final String COLUMN_CREDITS = "credits";
    public static final String COLUMN_IS_EXAM = "is_exam";
    public static final String COLUMN_IS_CREDIT = "is_credit";

    
    
    public static final String TABLE_GRADES = "grades";
    public static final String COLUMN_GRADE_ID = "id";
    public static final String COLUMN_GRADE_STUDENT_ID = "student_id";
    public static final String COLUMN_GRADE_SUBJECT_ID = "subject_id";
    public static final String COLUMN_GRADE_TEACHER_ID = "teacher_id";
    public static final String COLUMN_GRADE_VALUE = "value";
    public static final String COLUMN_GRADE_TYPE = "grade_type";
    
    private static final String GRADE_TYPE_CHECK =
            " CHECK(" + COLUMN_GRADE_TYPE + " IN ("
                    + "'CURRENT','CONTROL','TEST','PRACTICAL','EXAM','CREDIT','HOMEWORK',"
                    + "'ESSAY','TEST_PREP','LAB','ABSENT','EXEMPT','CREDIT_PASS'))";
    public static final String COLUMN_GRADE_COMMENT = "comment";
    public static final String COLUMN_GRADE_LESSON_ID = "lesson_id";
    public static final String COLUMN_GRADE_CREATED = "created_at";
    public static final String COLUMN_GRADE_UPDATED = "updated_at";
    public static final String COLUMN_GRADE_UPDATED_BY = "updated_by";
    public static final String COLUMN_GRADE_WEIGHT = "weight";

    
    
    public static final String TABLE_ATTENDANCE = "attendance";
    public static final String COLUMN_ATTENDANCE_ID = "id";
    public static final String COLUMN_ATTENDANCE_STUDENT_ID = "student_id";
    public static final String COLUMN_ATTENDANCE_LESSON_ID = "lesson_id";
    public static final String COLUMN_ATTENDANCE_DATE = "date";
    public static final String COLUMN_ATTENDANCE_STATUS = "status";
    public static final String COLUMN_ATTENDANCE_COMMENT = "comment";
    public static final String COLUMN_ATTENDANCE_MARKED = "marked_at";
    public static final String COLUMN_ATTENDANCE_MARKED_BY = "marked_by";

    
    
    public static final String TABLE_LESSONS = "lessons";
    public static final String COLUMN_LESSON_ID = "id";
    public static final String COLUMN_LESSON_GROUP_ID = "group_id";
    public static final String COLUMN_LESSON_SUBJECT_ID = "subject_id";
    public static final String COLUMN_LESSON_TEACHER_ID = "teacher_id";
    public static final String COLUMN_DAY_OF_WEEK = "day_of_week";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_CLASSROOM = "classroom";
    public static final String COLUMN_LESSON_TYPE = "lesson_type";
    public static final String COLUMN_WEEK_NUMBER = "week_number";
    public static final String COLUMN_IS_ALTERNATING = "is_alternating_week";
    public static final String COLUMN_LESSON_TOPIC = "lesson_topic";

    
    
    public static final String TABLE_ASSIGNMENTS = "assignments";
    public static final String COLUMN_ASSIGNMENT_ID = "id";
    public static final String COLUMN_ASSIGNMENT_TITLE = "title";
    public static final String COLUMN_ASSIGNMENT_DESC = "description";
    public static final String COLUMN_ASSIGNMENT_SUBJECT_ID = "subject_id";
    public static final String COLUMN_ASSIGNMENT_TEACHER_ID = "teacher_id";
    public static final String COLUMN_ASSIGNMENT_GROUP_ID = "group_id";
    public static final String COLUMN_ASSIGNED_DATE = "assigned_date";
    public static final String COLUMN_DUE_DATE = "due_date";
    public static final String COLUMN_ASSIGNMENT_TYPE = "assignment_type";
    public static final String COLUMN_ATTACHMENTS = "attachments";
    public static final String COLUMN_MAX_POINTS = "max_points";

    
    
    public static final String TABLE_COMPLETIONS = "homework_completions";
    public static final String COLUMN_COMPLETION_ID = "id";
    public static final String COLUMN_COMPLETION_ASSIGNMENT_ID = "assignment_id";
    public static final String COLUMN_COMPLETION_STUDENT_ID = "student_id";
    public static final String COLUMN_COMPLETION_STATUS = "status";
    public static final String COLUMN_STUDENT_COMMENT = "student_comment";
    public static final String COLUMN_SUBMITTED_AT = "submitted_at";
    public static final String COLUMN_RECEIVED_POINTS = "received_points";
    public static final String COLUMN_GRADED_AT = "graded_at";

    
    
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String COLUMN_NOTIFICATION_ID = "id";
    public static final String COLUMN_NOTIFICATION_USER_ID = "user_id";
    public static final String COLUMN_NOTIFICATION_TITLE = "title";
    public static final String COLUMN_NOTIFICATION_MESSAGE = "message";
    public static final String COLUMN_NOTIFICATION_TYPE = "notification_type";
    public static final String COLUMN_IS_READ = "is_read";
    public static final String COLUMN_NOTIFICATION_CREATED = "created_at";
    public static final String COLUMN_READ_AT = "read_at";
    public static final String COLUMN_ACTION_URL = "action_url";

    
    
    public static final String TABLE_ANNOUNCEMENTS = "announcements";
    public static final String COLUMN_ANNOUNCEMENT_ID = "id";
    public static final String COLUMN_ANNOUNCEMENT_TITLE = "title";
    public static final String COLUMN_ANNOUNCEMENT_CONTENT = "content";
    public static final String COLUMN_AUTHOR_ID = "author_id";
    public static final String COLUMN_TARGET_GROUP_ID = "target_group_id";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_PUBLISHED_AT = "published_at";
    public static final String COLUMN_EXPIRES_AT = "expires_at";
    public static final String COLUMN_IS_PUBLISHED = "is_published";
    public static final String COLUMN_IS_PINNED = "is_pinned";

    
    
    public static final String TABLE_ANNOUNCEMENT_READS = "announcement_reads";
    public static final String COLUMN_ANNOUNCEMENT_READ_ANN_ID = "announcement_id";
    public static final String COLUMN_ANNOUNCEMENT_READ_STUDENT_ID = "student_id";
    public static final String COLUMN_ANNOUNCEMENT_READ_AT = "read_at";

    
    
    public static final String TABLE_BREAKS = "schedule_breaks";
    public static final String COLUMN_BREAK_ID = "id";
    public static final String COLUMN_BREAK_GROUP_ID = "group_id";
    public static final String COLUMN_BREAK_DAY_OF_WEEK = "day_of_week";
    public static final String COLUMN_BREAK_START_TIME = "start_time";
    public static final String COLUMN_BREAK_END_TIME = "end_time";
    public static final String COLUMN_BREAK_LABEL = "label";

    
    
    public static final String TABLE_USER_SETTINGS = "user_settings";
    public static final String COLUMN_SETTING_NOTIFY_GRADES = "notify_new_grades";
    public static final String COLUMN_SETTING_NOTIFY_SCHEDULE = "notify_schedule_changes";
    public static final String COLUMN_SETTING_NOTIFY_HOMEWORK = "notify_homework";
    public static final String COLUMN_SETTING_NOTIFY_ANNOUNCE = "notify_announcements";
    public static final String COLUMN_SETTING_THEME = "theme";
    public static final String COLUMN_SETTING_LANGUAGE = "language";
    public static final String COLUMN_SETTING_LEARNING_SYSTEM = "learning_system";

    
    public static final String TABLE_SYNC_QUEUE = "sync_queue";
    public static final String SQ_ID = "_id";
    public static final String SQ_ENTITY = "entity";
    public static final String SQ_OP = "op";
    public static final String SQ_LOCAL_ID = "local_id";
    public static final String SQ_SERVER_ID = "server_id";
    public static final String SQ_PAYLOAD = "payload";
    public static final String SQ_DEPENDS_ON = "depends_on";
    public static final String SQ_ATTEMPTS = "attempts";
    public static final String SQ_NEXT_ATTEMPT_AT = "next_attempt_at";
    public static final String SQ_CREATED_AT = "created_at";

    public static final String TABLE_ID_MAP = "sync_id_map";
    public static final String IDM_ENTITY = "entity";
    public static final String IDM_LOCAL_ID = "local_id";
    public static final String IDM_SERVER_ID = "server_id";

    

    public DBHelper(@Nullable Context context) {
        super(context != null ? context.getApplicationContext() : null, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context != null ? context.getApplicationContext() : null;
        if (this.context != null) {
            sharedPreferences = this.context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        }
    }

    @Override
    public synchronized void close() {
        
        if (this == instance) {
            return;
        }
        super.close();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        
        
        db.enableWriteAheadLogging();
    }

    

    @Override
    public void onCreate(SQLiteDatabase db) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_user_uuid", UUID.randomUUID().toString());
        editor.apply();

        DatabaseCreator.createSchema(db);
    }

    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DatabaseCreator.upgradeSchema(db, oldVersion, newVersion);
    }

    





    

    public long insertUser(User user, String role) {
        String userId = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD_HASH, PasswordHasher.hashIfNeeded(user.getPasswordHash()));
        values.put(COLUMN_FIRST_NAME, user.getFirstName());
        values.put(COLUMN_LAST_NAME, user.getLastName());
        values.put(COLUMN_PATRONYMIC, user.getPatronymic());
        values.put(COLUMN_PHONE, user.getPhoneNumber());
        values.put(COLUMN_ROLE, role);
        values.put(COLUMN_IS_ACTIVE, user.isActive() ? 1 : 0);
        
        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(TABLE_USERS, null, values);
        
        
        if (result != -1) {
            createUserSettings(userId);
        }
        return result;
    }

    private void createUserSettings(String userId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        db.insert(TABLE_USER_SETTINGS, null, values);
    }

    public User findUserByEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, 
                COLUMN_EMAIL + " = ?", new String[]{email}, 
                null, null, null);
        
        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        return user;
    }

    @Nullable
    public User authenticateUser(String email, String plainPassword) {
        User user = findUserByEmail(email);
        if (user == null || !PasswordHasher.verify(plainPassword, user.getPasswordHash())) {
            return null;
        }
        updateUserLastLogin(user.getId().toString());
        return user;
    }

    @Nullable
    public String findUserRoleByUserId(String userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ROLE},
                COLUMN_USER_ID + " = ?", new String[]{userId},
                null, null, null);
        String role = null;
        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }
        cursor.close();
        return role;
    }

    public User findUserById(String userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, 
                COLUMN_USER_ID + " = ?", new String[]{userId}, 
                null, null, null);
        
        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        return user;
    }

    private User cursorToUser(Cursor cursor) {
        String role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
        User user;
        if ("TEACHER".equals(role)) {
            user = new Teacher();
        } else if ("STUDENT".equals(role)) {
            user = new Student();
        } else {
            user = new User() {
                @Override
                public UserRole getRole() {
                    return UserRole.ADMIN;
                }
            };
        }
        user.setId(safeUUID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
        user.setPasswordHash(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD_HASH)));
        user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)));
        user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)));
        user.setPatronymic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATRONYMIC)));
        user.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
        user.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ACTIVE)) == 1);
        user.setAvatarUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR)));
        return user;
    }

    public boolean updateUserAvatar(String userId, String avatarPath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AVATAR, avatarPath);
        return db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{userId}) > 0;
    }

    
    private void pushUserUpdateIfPossible(String userId) {
        if (context == null) return;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(TABLE_USERS, new String[]{
                            COLUMN_EMAIL, COLUMN_PASSWORD_HASH, COLUMN_FIRST_NAME, COLUMN_LAST_NAME,
                            COLUMN_PATRONYMIC, COLUMN_PHONE, COLUMN_ROLE, COLUMN_AVATAR},
                    COLUMN_USER_ID + " = ?", new String[]{userId}, null, null, null);
            if (!c.moveToFirst()) return;
            Map<String, Object> body = new HashMap<>();
            body.put("email", c.getString(0));
            body.put("passwordHash", c.getString(1));
            body.put("firstName", c.getString(2));
            body.put("lastName", c.getString(3));
            body.put("patronymic", c.getString(4));
            body.put("phoneNumber", c.getString(5));
            body.put("role", c.getString(6));
            body.put("avatarUrl", c.getString(7));
            ServerSyncManager.userUpdate(context, userId, body);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
    }

    public boolean updateUserPassword(String userId, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD_HASH, PasswordHasher.hashIfNeeded(newPassword));
        int rows = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{userId});
        if (rows > 0 && context != null) {
            pushUserUpdateIfPossible(userId);
        }
        return rows > 0;
    }

    
    public boolean updateUserProfile(String userId, String firstName, String lastName,
                                     String email, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        if (firstName != null && !firstName.isEmpty()) values.put(COLUMN_FIRST_NAME, firstName);
        if (lastName != null && !lastName.isEmpty()) values.put(COLUMN_LAST_NAME, lastName);
        if (email != null && !email.isEmpty()) values.put(COLUMN_EMAIL, email);
        if (newPassword != null && !newPassword.isEmpty()) {
            values.put(COLUMN_PASSWORD_HASH, PasswordHasher.hashIfNeeded(newPassword));
        }
        if (values.size() == 0) return false;
        int rows = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{userId});
        if (rows > 0 && context != null) {
            pushUserUpdateIfPossible(userId);
        }
        return rows > 0;
    }

    public boolean updateUserLastLogin(String userId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LAST_LOGIN, nowISO());
        int rows = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{userId});
        return rows > 0;
    }

    

    public long insertStudent(Student student, String userId, String groupId) {
        String studentId = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_ID, studentId);
        values.put(COLUMN_USER_ID_FK, userId);
        values.put(COLUMN_STUDENT_GROUP_ID, groupId);
        values.put(COLUMN_ENROLLMENT_DATE, student.getEnrollmentDate() != null ? 
                student.getEnrollmentDate().toString() : null);
        values.put(COLUMN_STUDENT_NUMBER, student.getStudentIdNumber());
        
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_STUDENTS, null, values);
    }

    public Student findStudentByUserId(String userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS, null, 
                COLUMN_USER_ID_FK + " = ?", new String[]{userId}, 
                null, null, null);
        
        Student student = null;
        if (cursor.moveToFirst()) {
            student = cursorToStudent(cursor);
        }
        cursor.close();
        return student;
    }

    private Student cursorToStudent(Cursor cursor) {
        Student student = new Student();
        student.setId(safeUUID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID))));
        String groupIdStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_GROUP_ID));
        if (groupIdStr != null) {
            StudentGroup group = new StudentGroup();
            group.setId(safeUUID(groupIdStr));
            student.setGroup(group);
        }
        String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENROLLMENT_DATE));
        if (dateStr != null) {
            student.setEnrollmentDate(java.time.LocalDate.parse(dateStr));
        }
        student.setStudentIdNumber(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NUMBER)));
        return student;
    }

    public List<Student> findStudentsByGroupId(String groupId) {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS, null, 
                COLUMN_STUDENT_GROUP_ID + " = ?", new String[]{groupId}, 
                null, null, null);
        
        while (cursor.moveToNext()) {
            students.add(cursorToStudent(cursor));
        }
        cursor.close();
        return students;
    }

    

    public long insertTeacher(Teacher teacher, String userId) {
        String teacherId = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEACHER_ID, teacherId);
        values.put(COLUMN_USER_ID_FK, userId);
        values.put(COLUMN_EMPLOYEE_ID, teacher.getEmployeeId());
        values.put(COLUMN_DEPARTMENT, teacher.getDepartment());
        
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_TEACHERS, null, values);
    }

    public Teacher findTeacherByUserId(String userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_TEACHERS, null, 
                COLUMN_USER_ID_FK + " = ?", new String[]{userId}, 
                null, null, null);
        
        Teacher teacher = null;
        if (cursor.moveToFirst()) {
            teacher = cursorToTeacher(cursor);
        }
        cursor.close();
        return teacher;
    }

    private Teacher cursorToTeacher(Cursor cursor) {
        Teacher teacher = new Teacher();
        teacher.setId(safeUUID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_ID))));
        teacher.setEmployeeId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMPLOYEE_ID)));
        teacher.setDepartment(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEPARTMENT)));
        return teacher;
    }

    

    public long insertGrade(Grade grade) {
        String gradeId = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GRADE_ID, gradeId);
        values.put(COLUMN_GRADE_STUDENT_ID, grade.getStudent().getId().toString());
        values.put(COLUMN_GRADE_SUBJECT_ID, grade.getSubject().getId().toString());
        values.put(COLUMN_GRADE_TEACHER_ID, grade.getTeacher().getId().toString());
        values.put(COLUMN_GRADE_VALUE, grade.getValue());
        values.put(COLUMN_GRADE_TYPE, grade.getType().name());
        values.put(COLUMN_GRADE_COMMENT, grade.getComment());
        values.put(COLUMN_GRADE_LESSON_ID, grade.getLessonId() != null ? grade.getLessonId().toString() : null);
        values.put(COLUMN_GRADE_WEIGHT, grade.getWeight() != null ? grade.getWeight() : 1);
        
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_GRADES, null, values);
    }

    public List<Grade> findAllGrades() {
        List<Grade> grades = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRADES, null, null, null, null, null, COLUMN_GRADE_CREATED + " DESC");
        
        while (cursor.moveToNext()) {
            grades.add(cursorToGrade(cursor));
        }
        cursor.close();
        return grades;
    }

    public List<Grade> findGradesByStudentId(String studentId) {
        List<Grade> grades = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRADES, null, 
                COLUMN_GRADE_STUDENT_ID + " = ?", new String[]{studentId}, 
                null, null, COLUMN_GRADE_CREATED + " DESC");
        
        while (cursor.moveToNext()) {
            grades.add(cursorToGrade(cursor));
        }
        cursor.close();
        return grades;
    }

    public List<Grade> findGradesByStudentAndSubject(String studentId, String subjectId) {
        List<Grade> grades = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRADES, null, 
                COLUMN_GRADE_STUDENT_ID + " = ? AND " + COLUMN_GRADE_SUBJECT_ID + " = ?", 
                new String[]{studentId, subjectId}, 
                null, null, COLUMN_GRADE_CREATED + " DESC");
        
        while (cursor.moveToNext()) {
            grades.add(cursorToGrade(cursor));
        }
        cursor.close();
        return grades;
    }

    private Grade cursorToGrade(Cursor cursor) {
        Grade grade = new Grade();
        grade.setId(safeUUID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRADE_ID))));
        grade.setValue(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GRADE_VALUE)));
        String typeStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRADE_TYPE));
        try {
            grade.setType(Grade.GradeType.valueOf(typeStr));
        } catch (IllegalArgumentException e) {
            grade.setType(Grade.GradeType.CURRENT);
        }
        grade.setComment(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRADE_COMMENT)));
        String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRADE_CREATED));
        if (dateStr != null) {
            grade.setCreatedAt(java.time.LocalDateTime.parse(dateStr));
        }
        
        int updatedByCol = cursor.getColumnIndex(COLUMN_GRADE_UPDATED_BY);
        if (updatedByCol != -1 && !cursor.isNull(updatedByCol)) {
            Teacher updatedBy = new Teacher();
            updatedBy.setId(safeUUID(cursor.getString(updatedByCol)));
            grade.setUpdatedBy(updatedBy);
        }
        
        int weightCol = cursor.getColumnIndex(COLUMN_GRADE_WEIGHT);
        if (weightCol != -1 && !cursor.isNull(weightCol)) {
            grade.setWeight(cursor.getInt(weightCol));
        } else {
            grade.setWeight(1); 
        }
        
        return grade;
    }

    public boolean updateGrade(String gradeId, Integer newValue, String comment, String updatedByTeacherId, Integer weight) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GRADE_VALUE, newValue);
        values.put(COLUMN_GRADE_COMMENT, comment);
        if (weight != null) {
            values.put(COLUMN_GRADE_WEIGHT, weight);
        }
        if (updatedByTeacherId != null) {
            values.put(COLUMN_GRADE_UPDATED_BY, updatedByTeacherId);
        }
        int rows = db.update(TABLE_GRADES, values, COLUMN_GRADE_ID + " = ?", new String[]{gradeId});
        return rows > 0;
    }

    

    public long insertAttendance(Attendance attendance) {
        String attendanceId = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ATTENDANCE_ID, attendanceId);
        values.put(COLUMN_ATTENDANCE_STUDENT_ID, attendance.getStudent().getId().toString());
        values.put(COLUMN_ATTENDANCE_LESSON_ID, attendance.getLessonId() != null ? 
                attendance.getLessonId().toString() : null);
        values.put(COLUMN_ATTENDANCE_DATE, attendance.getDate().toString());
        values.put(COLUMN_ATTENDANCE_STATUS, attendance.getStatus().name());
        values.put(COLUMN_ATTENDANCE_COMMENT, attendance.getComment());
        values.put(COLUMN_ATTENDANCE_MARKED_BY, attendance.getMarkedBy() != null ? 
                attendance.getMarkedBy().getId().toString() : null);
        
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_ATTENDANCE, null, values);
    }

    public List<Attendance> findAttendanceByStudentAndDateRange(String studentId, 
            java.time.LocalDate from, java.time.LocalDate to) {
        List<Attendance> attendanceList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ATTENDANCE, null, 
                COLUMN_ATTENDANCE_STUDENT_ID + " = ? AND " + COLUMN_ATTENDANCE_DATE + " BETWEEN ? AND ?", 
                new String[]{studentId, from.toString(), to.toString()}, 
                null, null, COLUMN_ATTENDANCE_DATE + " DESC");
        
        while (cursor.moveToNext()) {
            attendanceList.add(cursorToAttendance(cursor));
        }
        cursor.close();
        return attendanceList;
    }

    private Attendance cursorToAttendance(Cursor cursor) {
        Attendance attendance = new Attendance();
        attendance.setId(safeUUID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTENDANCE_ID))));
        attendance.setDate(java.time.LocalDate.parse(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTENDANCE_DATE))));
        attendance.setStatus(Attendance.AttendanceStatus.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTENDANCE_STATUS))));
        attendance.setComment(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ATTENDANCE_COMMENT)));

        int markedByCol = cursor.getColumnIndex(COLUMN_ATTENDANCE_MARKED_BY);
        if (markedByCol != -1 && !cursor.isNull(markedByCol)) {
            Teacher markedBy = new Teacher();
            markedBy.setId(safeUUID(cursor.getString(markedByCol)));
            attendance.setMarkedBy(markedBy);
        }

        return attendance;
    }

    

    public long insertAssignment(Assignment assignment) {
        String assignmentId = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ASSIGNMENT_ID, assignmentId);
        values.put(COLUMN_ASSIGNMENT_TITLE, assignment.getTitle());
        values.put(COLUMN_ASSIGNMENT_DESC, assignment.getDescription());
        values.put(COLUMN_ASSIGNMENT_SUBJECT_ID, assignment.getSubject().getId().toString());
        values.put(COLUMN_ASSIGNMENT_TEACHER_ID, assignment.getTeacher().getId().toString());
        values.put(COLUMN_ASSIGNMENT_GROUP_ID, assignment.getGroup().getId().toString());
        values.put(COLUMN_ASSIGNED_DATE, assignment.getAssignedDate().toString());
        values.put(COLUMN_DUE_DATE, assignment.getDueDate() != null ? 
                assignment.getDueDate().toString() : null);
        values.put(COLUMN_ASSIGNMENT_TYPE, assignment.getType().name());
        values.put(COLUMN_ATTACHMENTS, assignment.getAttachments());
        values.put(COLUMN_MAX_POINTS, assignment.getMaxPoints());
        
        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(TABLE_ASSIGNMENTS, null, values);
        
        
        if (result != -1) {
            createHomeworkCompletions(assignmentId, assignment.getGroup().getId().toString());
        }
        return result;
    }

    private void createHomeworkCompletions(String assignmentId, String groupId) {
        SQLiteDatabase db = getWritableDatabase();
        List<Student> students = findStudentsByGroupId(groupId);
        for (Student student : students) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_COMPLETION_ID, UUID.randomUUID().toString());
            values.put(COLUMN_COMPLETION_ASSIGNMENT_ID, assignmentId);
            values.put(COLUMN_COMPLETION_STUDENT_ID, student.getId().toString());
            values.put(COLUMN_COMPLETION_STATUS, "NOT_STARTED");
            db.insert(TABLE_COMPLETIONS, null, values);
        }
    }

    public List<Assignment> findAssignmentsByGroupId(String groupId) {
        List<Assignment> assignments = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ASSIGNMENTS, null, 
                COLUMN_ASSIGNMENT_GROUP_ID + " = ?", new String[]{groupId}, 
                null, null, COLUMN_DUE_DATE + " ASC");
        
        while (cursor.moveToNext()) {
            assignments.add(cursorToAssignment(cursor));
        }
        cursor.close();
        return assignments;
    }

    private Assignment cursorToAssignment(Cursor cursor) {
        Assignment assignment = new Assignment();
        assignment.setId(safeUUID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASSIGNMENT_ID))));
        assignment.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASSIGNMENT_TITLE)));
        assignment.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASSIGNMENT_DESC)));
        assignment.setAssignedDate(java.time.LocalDate.parse(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASSIGNED_DATE))));
        String dueDateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE));
        if (dueDateStr != null) {
            assignment.setDueDate(java.time.LocalDate.parse(dueDateStr));
        }
        assignment.setType(Assignment.AssignmentType.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ASSIGNMENT_TYPE))));
        return assignment;
    }

    

    public boolean updateHomeworkCompletion(String assignmentId, String studentId, 
            String status, String comment) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLETION_STATUS, status);
        values.put(COLUMN_STUDENT_COMMENT, comment);
        if ("COMPLETED".equals(status) || "SUBMITTED".equals(status)) {
            values.put(COLUMN_SUBMITTED_AT, nowISO());
        }
        int rows = db.update(TABLE_COMPLETIONS, values, 
                COLUMN_COMPLETION_ASSIGNMENT_ID + " = ? AND " + COLUMN_COMPLETION_STUDENT_ID + " = ?", 
                new String[]{assignmentId, studentId});
        if (rows > 0) {
            pushHomeworkCompletionIfPossible(assignmentId, studentId);
            return true;
        }
        
        String localId = UUID.randomUUID().toString();
        values.put(COLUMN_COMPLETION_ID, localId);
        values.put(COLUMN_COMPLETION_ASSIGNMENT_ID, assignmentId);
        values.put(COLUMN_COMPLETION_STUDENT_ID, studentId);
        long result = db.insertWithOnConflict(TABLE_COMPLETIONS, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
        if (result != -1) {
            pushHomeworkCompletionIfPossible(assignmentId, studentId);
            return true;
        }
        return false;
    }

    private void pushHomeworkCompletionIfPossible(String assignmentId, String studentId) {
        if (context == null) return;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(TABLE_COMPLETIONS,
                    new String[]{COLUMN_COMPLETION_ID, COLUMN_COMPLETION_STATUS, COLUMN_STUDENT_COMMENT, COLUMN_SUBMITTED_AT, COLUMN_RECEIVED_POINTS, COLUMN_GRADED_AT},
                    COLUMN_COMPLETION_ASSIGNMENT_ID + " = ? AND " + COLUMN_COMPLETION_STUDENT_ID + " = ?",
                    new String[]{assignmentId, studentId},
                    null, null, null);
            if (!c.moveToFirst()) return;
            String completionId = c.getString(0);
            Map<String, Object> body = new HashMap<>();
            body.put("assignmentId", assignmentId);
            body.put("studentId", studentId);
            body.put("status", c.getString(1));
            body.put("studentComment", c.getString(2));
            body.put("submittedAt", c.getString(3));
            if (!c.isNull(4)) body.put("receivedPoints", c.getInt(4));
            body.put("gradedAt", c.getString(5));
            ServerSyncManager.homeworkCompletionUpdate(context, completionId, body);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
    }

    public HomeworkCompletion findCompletion(String assignmentId, String studentId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_COMPLETIONS, null, 
                COLUMN_COMPLETION_ASSIGNMENT_ID + " = ? AND " + COLUMN_COMPLETION_STUDENT_ID + " = ?", 
                new String[]{assignmentId, studentId}, 
                null, null, null);
        
        HomeworkCompletion completion = null;
        if (cursor.moveToFirst()) {
            completion = cursorToCompletion(cursor);
        }
        cursor.close();
        return completion;
    }

    private HomeworkCompletion cursorToCompletion(Cursor cursor) {
        HomeworkCompletion completion = new HomeworkCompletion();
        completion.setId(safeUUID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPLETION_ID))));
        completion.setStatus(HomeworkCompletion.CompletionStatus.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPLETION_STATUS))));
        completion.setStudentComment(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_COMMENT)));
        int idxPoints = cursor.getColumnIndexOrThrow(COLUMN_RECEIVED_POINTS);
        completion.setReceivedPoints(cursor.isNull(idxPoints) ? null : cursor.getInt(idxPoints));
        return completion;
    }

    

    public long insertNotification(String recipientId, String title, String message, String type) {
        String notifyUserId = resolveUserIdForStudent(recipientId);
        if (notifyUserId == null) notifyUserId = recipientId;

        if (!shouldSendNotification(notifyUserId, type)) {
            return -1;
        }
        
        String notificationId = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFICATION_ID, notificationId);
        values.put(COLUMN_NOTIFICATION_USER_ID, notifyUserId);
        values.put(COLUMN_NOTIFICATION_TITLE, title);
        values.put(COLUMN_NOTIFICATION_MESSAGE, message);
        values.put(COLUMN_NOTIFICATION_TYPE, type);
        values.put(COLUMN_IS_READ, 0);
        
        SQLiteDatabase db = getWritableDatabase();
        long res = db.insert(TABLE_NOTIFICATIONS, null, values);
        if (res != -1 && context != null) {
            Map<String, Object> body = new HashMap<>();
            Integer uid = safeApiInt(notifyUserId);
            if (uid != null) body.put("userId", uid);
            body.put("title", title);
            body.put("message", message);
            body.put("notificationType", type);
            body.put("isRead", 0);
            body.put("createdAt", nowISO());
            ServerSyncManager.notificationCreate(context, body);
        }
        return res;
    }

    private boolean shouldSendNotification(String userId, String type) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER_SETTINGS, null, 
                COLUMN_USER_ID + " = ?", new String[]{userId}, 
                null, null, null);
        
        boolean shouldSend = true;
        if (cursor.moveToFirst()) {
            switch (type) {
                case "NEW_GRADE":
                    shouldSend = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SETTING_NOTIFY_GRADES)) == 1;
                    break;
                case "SCHEDULE_CHANGE":
                    shouldSend = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SETTING_NOTIFY_SCHEDULE)) == 1;
                    break;
                case "NEW_HOMEWORK":
                case "HOMEWORK_DEADLINE":
                    shouldSend = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SETTING_NOTIFY_HOMEWORK)) == 1;
                    break;
                case "ANNOUNCEMENT":
                    shouldSend = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SETTING_NOTIFY_ANNOUNCE)) == 1;
                    break;
                default:
                    shouldSend = true;
                    break;
            }
        }
        cursor.close();
        return shouldSend;
    }

    public List<Notification> findUnreadNotifications(String userId) {
        List<Notification> notifications = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTIFICATIONS, null, 
                COLUMN_NOTIFICATION_USER_ID + " = ? AND " + COLUMN_IS_READ + " = 0", 
                new String[]{userId}, 
                null, null, COLUMN_NOTIFICATION_CREATED + " DESC");
        
        while (cursor.moveToNext()) {
            notifications.add(cursorToNotification(cursor));
        }
        cursor.close();
        return notifications;
    }

    private Notification cursorToNotification(Cursor cursor) {
        Notification notification = new Notification();
        notification.setId(safeUUID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_ID))));
        notification.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_TITLE)));
        notification.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_MESSAGE)));
        notification.setType(Notification.NotificationType.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_TYPE))));
        notification.setRead(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_READ)) == 1);
        return notification;
    }

    public boolean markNotificationAsRead(String notificationId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_READ, 1);
        values.put(COLUMN_READ_AT, nowISO());
        int rows = db.update(TABLE_NOTIFICATIONS, values, 
                COLUMN_NOTIFICATION_ID + " = ?", new String[]{notificationId});
        if (rows > 0 && context != null) {
            pushNotificationReadIfPossible(notificationId);
        }
        return rows > 0;
    }

    
    private void pushNotificationReadIfPossible(String notificationId) {
        if (context == null) return;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(TABLE_NOTIFICATIONS, new String[]{
                            COLUMN_NOTIFICATION_USER_ID, COLUMN_NOTIFICATION_TITLE,
                            COLUMN_NOTIFICATION_MESSAGE, COLUMN_NOTIFICATION_TYPE,
                            COLUMN_NOTIFICATION_CREATED, COLUMN_ACTION_URL},
                    COLUMN_NOTIFICATION_ID + " = ?", new String[]{notificationId},
                    null, null, null);
            if (!c.moveToFirst()) return;
            Map<String, Object> body = new HashMap<>();
            body.put("userId", safeApiInt(c.getString(0)));
            body.put("title", c.getString(1));
            body.put("message", c.getString(2));
            body.put("notificationType", c.getString(3));
            body.put("isRead", true);
            body.put("createdAt", toIsoDateTime(c.getString(4)));
            body.put("readAt", nowISO());
            body.put("actionUrl", c.getString(5));
            ServerSyncManager.notificationUpdate(context, notificationId, body);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
    }

    
    private String toIsoDateTime(String value) {
        if (value == null || value.isEmpty()) return null;
        String v = value.trim();
        if (v.length() >= 19 && v.charAt(10) == ' ') {
            v = v.substring(0, 10) + "T" + v.substring(11, 19);
        } else if (v.length() == 10) {
            v = v + "T00:00:00";
        }
        return v;
    }

    

    public long insertAnnouncement(Announcement announcement) {
        String announcementId = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ANNOUNCEMENT_ID, announcementId);
        values.put(COLUMN_ANNOUNCEMENT_TITLE, announcement.getTitle());
        values.put(COLUMN_ANNOUNCEMENT_CONTENT, announcement.getContent());
        values.put(COLUMN_AUTHOR_ID, announcement.getAuthor().getId().toString());
        values.put(COLUMN_TARGET_GROUP_ID, announcement.getTargetGroup() != null ? 
                announcement.getTargetGroup().getId().toString() : null);
        values.put(COLUMN_PRIORITY, announcement.getPriority().name());
        values.put(COLUMN_IS_PUBLISHED, announcement.isPublished() ? 1 : 0);
        values.put(COLUMN_IS_PINNED, announcement.isPinned() ? 1 : 0);
        values.put(COLUMN_ATTACHMENTS, announcement.getAttachmentUrls() != null ? 
                String.join(",", announcement.getAttachmentUrls()) : null);
        
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_ANNOUNCEMENTS, null, values);
    }

    public List<Announcement> findAllAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ANNOUNCEMENTS, null, null, null, null, null, COLUMN_CREATED_AT + " DESC");
        
        while (cursor.moveToNext()) {
            announcements.add(cursorToAnnouncement(cursor));
        }
        cursor.close();
        return announcements;
    }

    public List<Announcement> findActiveAnnouncementsByGroupId(String groupId) {
        List<Announcement> announcements = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ANNOUNCEMENTS, null, 
                "(" + COLUMN_TARGET_GROUP_ID + " = ? OR " + COLUMN_TARGET_GROUP_ID + " IS NULL) AND " +
                COLUMN_IS_PUBLISHED + " = 1 AND (" + COLUMN_EXPIRES_AT + " IS NULL OR " + 
                COLUMN_EXPIRES_AT + " > datetime('now'))", 
                new String[]{groupId}, 
                null, null, COLUMN_IS_PINNED + " DESC, " + COLUMN_PRIORITY + " DESC, " + COLUMN_CREATED_AT + " DESC");
        
        while (cursor.moveToNext()) {
            announcements.add(cursorToAnnouncement(cursor));
        }
        cursor.close();
        return announcements;
    }

    private Announcement cursorToAnnouncement(Cursor cursor) {
        Announcement announcement = new Announcement();
        announcement.setId(safeUUID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOUNCEMENT_ID))));
        announcement.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOUNCEMENT_TITLE)));
        announcement.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOUNCEMENT_CONTENT)));

        int priorityIdx = cursor.getColumnIndexOrThrow(COLUMN_PRIORITY);
        if (!cursor.isNull(priorityIdx)) {
            announcement.setPriority(Announcement.AnnouncementPriority.valueOf(cursor.getString(priorityIdx)));
        }

        announcement.setPublished(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_PUBLISHED)) == 1);
        announcement.setPinned(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_PINNED)) == 1);

        int createdAtIdx = cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT);
        if (!cursor.isNull(createdAtIdx)) {
            try {
                announcement.setCreatedAt(LocalDateTime.parse(
                        cursor.getString(createdAtIdx).replace(" ", "T"),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } catch (Exception ignored) {}
        }

        return announcement;
    }

    public boolean markAnnouncementAsRead(String announcementId, String studentId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ANNOUNCEMENT_READ_ANN_ID, announcementId);
        values.put(COLUMN_ANNOUNCEMENT_READ_STUDENT_ID, studentId);
        values.put(COLUMN_ANNOUNCEMENT_READ_AT, nowISO());
        
        long result = db.insertWithOnConflict(TABLE_ANNOUNCEMENT_READS, null, values, 
                SQLiteDatabase.CONFLICT_IGNORE);
        if (result != -1 && context != null) {
            Map<String, Object> body = new HashMap<>();
            body.put("announcementId", safeApiInt(announcementId));
            body.put("studentId", safeApiInt(studentId));
            body.put("readAt", nowISO());
            ServerSyncManager.announcementReadCreate(context, body);
        }
        return result != -1;
    }

    public boolean isAnnouncementRead(String announcementId, String studentId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ANNOUNCEMENT_READS, null, 
                COLUMN_ANNOUNCEMENT_READ_ANN_ID + " = ? AND " + COLUMN_ANNOUNCEMENT_READ_STUDENT_ID + " = ?", 
                new String[]{announcementId, studentId}, 
                null, null, null);
        
        boolean isRead = cursor.getCount() > 0;
        cursor.close();
        return isRead;
    }

    

    public boolean updateNotificationSetting(String userId, String settingKey, boolean enabled) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        
        switch (settingKey) {
            case "newGrades":
                values.put(COLUMN_SETTING_NOTIFY_GRADES, enabled ? 1 : 0);
                break;
            case "scheduleChanges":
                values.put(COLUMN_SETTING_NOTIFY_SCHEDULE, enabled ? 1 : 0);
                break;
            case "homework":
                values.put(COLUMN_SETTING_NOTIFY_HOMEWORK, enabled ? 1 : 0);
                break;
            case "announcements":
                values.put(COLUMN_SETTING_NOTIFY_ANNOUNCE, enabled ? 1 : 0);
                break;
            default:
                return false;
        }
        
        int rows = db.update(TABLE_USER_SETTINGS, values, 
                COLUMN_USER_ID + " = ?", new String[]{userId});
        if (rows > 0 && context != null) {
            pushUserSettingsIfPossible(userId);
        }
        return rows > 0;
    }

    
    private void pushUserSettingsIfPossible(String userId) {
        if (context == null) return;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(TABLE_USER_SETTINGS, new String[]{
                            COLUMN_SETTING_NOTIFY_GRADES, COLUMN_SETTING_NOTIFY_SCHEDULE,
                            COLUMN_SETTING_NOTIFY_HOMEWORK, COLUMN_SETTING_NOTIFY_ANNOUNCE,
                            COLUMN_SETTING_THEME, COLUMN_SETTING_LANGUAGE},
                    COLUMN_USER_ID + " = ?", new String[]{userId}, null, null, null);
            if (!c.moveToFirst()) return;
            Map<String, Object> body = new HashMap<>();
            Integer uid = safeApiInt(userId);
            if (uid != null) body.put("userId", uid);
            body.put("notifyNewGrades", c.getInt(0) == 1);
            body.put("notifyScheduleChanges", c.getInt(1) == 1);
            body.put("notifyHomework", c.getInt(2) == 1);
            body.put("notifyAnnouncements", c.getInt(3) == 1);
            body.put("theme", c.getString(4));
            body.put("language", c.getString(5));
            ServerSyncManager.userSettingsUpdate(context, userId, body);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
    }

    public HashMap<String, Boolean> getNotificationSettings(String userId) {
        HashMap<String, Boolean> settings = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER_SETTINGS, null, 
                COLUMN_USER_ID + " = ?", new String[]{userId}, 
                null, null, null);
        
        if (cursor.moveToFirst()) {
            settings.put("newGrades", cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SETTING_NOTIFY_GRADES)) == 1);
            settings.put("scheduleChanges", cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SETTING_NOTIFY_SCHEDULE)) == 1);
            settings.put("homework", cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SETTING_NOTIFY_HOMEWORK)) == 1);
            settings.put("announcements", cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SETTING_NOTIFY_ANNOUNCE)) == 1);
        }
        cursor.close();
        return settings;
    }

    

    private java.util.UUID safeUUID(String id) {
        try {
            return java.util.UUID.fromString(id);
        } catch (Exception e) {
            return java.util.UUID.nameUUIDFromBytes(id.getBytes());
        }
    }

    private String nowISO() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public void closeDatabase() {
        this.close();
    }

    public void clearAllData() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM " + TABLE_ANNOUNCEMENT_READS);
            db.execSQL("DELETE FROM " + TABLE_USER_SETTINGS);
            db.execSQL("DELETE FROM " + TABLE_COMPLETIONS);
            db.execSQL("DELETE FROM " + TABLE_ASSIGNMENTS);
            db.execSQL("DELETE FROM " + TABLE_NOTIFICATIONS);
            db.execSQL("DELETE FROM " + TABLE_ANNOUNCEMENTS);
            db.execSQL("DELETE FROM " + TABLE_ATTENDANCE);
            db.execSQL("DELETE FROM " + TABLE_GRADES);
            db.execSQL("DELETE FROM " + TABLE_LESSONS);
            db.execSQL("DELETE FROM " + TABLE_STUDENTS);
            db.execSQL("DELETE FROM " + TABLE_TEACHERS);
            db.execSQL("DELETE FROM " + TABLE_GROUPS);
            db.execSQL("DELETE FROM " + TABLE_SUBJECTS);
            db.execSQL("DELETE FROM " + TABLE_USERS);
            
            db.execSQL("DELETE FROM " + TABLE_SYNC_QUEUE);
            db.execSQL("DELETE FROM " + TABLE_ID_MAP);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    

    
    public Cursor findLessonsForGroupRaw(String groupId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT l.id, l.day_of_week, l.start_time, l.end_time, l.classroom, l.lesson_type, "
                   + "s.name AS subject_name, "
                   + "u.last_name || ' ' || SUBSTR(u.first_name,1,1) || '.' || COALESCE(SUBSTR(u.patronymic,1,1) || '.','') AS teacher_name "
                   + "FROM " + TABLE_LESSONS + " l "
                   + "JOIN " + TABLE_SUBJECTS + " s ON l.subject_id = s.id "
                   + "JOIN " + TABLE_TEACHERS + " t ON l.teacher_id = t.id "
                   + "JOIN " + TABLE_USERS + " u ON t.user_id = u.id "
                   + "WHERE l.group_id = ? "
                   + "ORDER BY l.day_of_week, l.start_time";
        return db.rawQuery(sql, new String[]{groupId});
    }

    

    
    public Cursor findScheduleForGroupDayRaw(String groupId, int dayOfWeek) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT l.id AS _id, l.start_time, l.end_time, l.classroom, l.lesson_type, "
                + "'lesson' AS item_type, "
                + "s.id AS subject_id, "
                + "s.name AS subject_name, "
                + "u.last_name || ' ' || SUBSTR(u.first_name,1,1) || '.' || COALESCE(SUBSTR(u.patronymic,1,1) || '.','') AS teacher_name, "
                + "'' AS label "
                + "FROM " + TABLE_LESSONS + " l "
                + "JOIN " + TABLE_SUBJECTS + " s ON l.subject_id = s.id "
                + "JOIN " + TABLE_TEACHERS + " t ON l.teacher_id = t.id "
                + "JOIN " + TABLE_USERS + " u ON t.user_id = u.id "
                + "WHERE l.group_id = ? AND l.day_of_week = ? "
                + "UNION ALL "
                + "SELECT b.id AS _id, b.start_time, b.end_time, '' AS classroom, '' AS lesson_type, "
                + "'break' AS item_type, "
                + "'' AS subject_id, '' AS subject_name, '' AS teacher_name, b.label "
                + "FROM " + TABLE_BREAKS + " b "
                + "WHERE (b.group_id = ? OR b.group_id IS NULL) AND (b.day_of_week = ? OR b.day_of_week IS NULL) "
                + "ORDER BY start_time";
        return db.rawQuery(sql, new String[]{groupId, String.valueOf(dayOfWeek), groupId, String.valueOf(dayOfWeek)});
    }

    

    
    public Cursor findAllNotificationsForUserRaw(String userId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT id, title, message, notification_type, is_read, created_at "
                   + "FROM " + TABLE_NOTIFICATIONS
                   + " WHERE user_id = ? ORDER BY created_at DESC";
        Cursor cursor = db.rawQuery(sql, new String[]{userId});
        Log.d("NOTIF_DEBUG", "findAllNotificationsForUserRaw(" + userId + ") returned " + cursor.getCount() + " rows");
        if (cursor.getCount() == 0) {
            
            Cursor all = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS, null);
            all.moveToFirst();
            Log.d("NOTIF_DEBUG", "Total notifications in DB: " + all.getInt(0));
            all.close();
        }
        return cursor;
    }

    

    
    public Cursor findAttendanceForStudentRaw(String studentId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT a.id, a.date, a.status, a.comment, "
                   + "s.name AS subject_name, a.lesson_id, "
                   + "l.start_time AS start_time, l.end_time AS end_time "
                   + "FROM " + TABLE_ATTENDANCE + " a "
                   + "LEFT JOIN " + TABLE_LESSONS + " l ON a.lesson_id = l.id "
                   + "LEFT JOIN " + TABLE_SUBJECTS + " s ON l.subject_id = s.id "
                   + "WHERE a.student_id = ? "
                   + "ORDER BY a.date DESC, a.id";
        return db.rawQuery(sql, new String[]{studentId});
    }

    

    
    public Cursor findAssignmentsForStudentRaw(String studentId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT a.id, a.title, a.description, a.due_date, a.assignment_type, "
                   + "s.name AS subject_name, "
                   + "COALESCE(c.status, 'NOT_STARTED') AS completion_status, "
                   + "c.submitted_at "
                   + "FROM " + TABLE_ASSIGNMENTS + " a "
                   + "JOIN " + TABLE_SUBJECTS + " s ON a.subject_id = s.id "
                   + "JOIN " + TABLE_STUDENTS + " st ON st.group_id = a.group_id "
                   + "LEFT JOIN " + TABLE_COMPLETIONS + " c ON c.assignment_id = a.id AND c.student_id = st.id "
                   + "WHERE st.id = ? "
                   + "ORDER BY a.due_date ASC";
        return db.rawQuery(sql, new String[]{studentId});
    }

    

    
    public Cursor findGradesForStudentRaw(String studentId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT g.id, g.value, g.grade_type, g.weight, g.comment, g.created_at, "
                   + "s.name AS subject_name, "
                   + "COALESCE(u.last_name || ' ' || SUBSTR(u.first_name,1,1) || '.' || COALESCE(SUBSTR(u.patronymic,1,1) || '.',''), '—') AS teacher_name "
                   + "FROM " + TABLE_GRADES + " g "
                   + "JOIN " + TABLE_SUBJECTS + " s ON g.subject_id = s.id "
                   + "LEFT JOIN " + TABLE_TEACHERS + " t ON g.teacher_id = t.id "
                   + "LEFT JOIN " + TABLE_USERS + " u ON t.user_id = u.id "
                   + "WHERE g.student_id = ? "
                   + "ORDER BY g.created_at DESC";
        return db.rawQuery(sql, new String[]{studentId});
    }

    
    public Cursor findSubjectsWithAverageForStudent(String studentId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT l.subject_id, s.name AS subject_name, "
                   + "ROUND(AVG(CAST(g.value AS REAL)), 2) AS avg_grade, "
                   + "COUNT(DISTINCT g.id) AS grade_count, "
                   + "u.last_name || ' ' || u.first_name || ' ' || COALESCE(u.patronymic, '') AS teacher_full_name "
                   + "FROM " + TABLE_LESSONS + " l "
                   + "JOIN " + TABLE_SUBJECTS + " s ON l.subject_id = s.id "
                   + "JOIN " + TABLE_TEACHERS + " t ON l.teacher_id = t.id "
                   + "JOIN " + TABLE_USERS + " u ON t.user_id = u.id "
                   + "LEFT JOIN " + TABLE_GRADES + " g ON g.subject_id = l.subject_id AND g.student_id = ? "
                   + "WHERE l.group_id = (SELECT group_id FROM " + TABLE_STUDENTS + " WHERE id = ?) "
                   + "GROUP BY l.subject_id "
                   + "ORDER BY s.name";
        return db.rawQuery(sql, new String[]{studentId, studentId});
    }

    public String findSubjectNameById(String subjectId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_SUBJECTS, new String[]{COLUMN_SUBJECT_NAME},
                COLUMN_SUBJECT_ID + " = ?", new String[]{subjectId}, null, null, null);
        String name = null;
        if (c.moveToFirst()) {
            name = c.getString(c.getColumnIndexOrThrow(COLUMN_SUBJECT_NAME));
        }
        c.close();
        return name;
    }

    
    public Cursor findGradesBySubjectForStudentRaw(String studentId, String subjectId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT g.id, g.value, g.grade_type, g.weight, g.comment, g.created_at, "
                   + "COALESCE(u.last_name || ' ' || u.first_name || ' ' || COALESCE(u.patronymic, ''), '—') AS teacher_name "
                   + "FROM " + TABLE_GRADES + " g "
                   + "LEFT JOIN " + TABLE_TEACHERS + " t ON g.teacher_id = t.id "
                   + "LEFT JOIN " + TABLE_USERS + " u ON t.user_id = u.id "
                   + "WHERE g.student_id = ? AND g.subject_id = ? "
                   + "ORDER BY g.created_at DESC";
        return db.rawQuery(sql, new String[]{studentId, subjectId});
    }

    
    public int findAbsenceCountBySubject(String studentId, String subjectId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + TABLE_ATTENDANCE + " a "
                   + "JOIN " + TABLE_LESSONS + " l ON a.lesson_id = l.id "
                   + "WHERE a.student_id = ? AND l.subject_id = ? AND a.status IN ('ABSENT', 'EXCUSED_ABSENT')";
        Cursor c = db.rawQuery(sql, new String[]{studentId, subjectId});
        int count = 0;
        if (c.moveToFirst()) {
            count = c.getInt(0);
        }
        c.close();
        return count;
    }

    

    
    public Cursor findLessonsForTeacherRaw(String teacherId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT l.id, l.day_of_week, l.start_time, l.end_time, l.classroom, l.lesson_type, "
                   + "s.name AS subject_name, g.code AS group_code "
                   + "FROM " + TABLE_LESSONS + " l "
                   + "JOIN " + TABLE_SUBJECTS + " s ON l.subject_id = s.id "
                   + "JOIN " + TABLE_GROUPS + " g ON l.group_id = g.id "
                   + "WHERE l.teacher_id = ? "
                   + "ORDER BY l.day_of_week, l.start_time";
        return db.rawQuery(sql, new String[]{teacherId});
    }

    
    public Cursor findTeacherLessonsForDayRaw(String teacherId, int dayOfWeek) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT l.id, l.day_of_week, l.start_time, l.end_time, l.classroom, l.lesson_type, "
                   + "s.name AS subject_name, g.code AS group_code "
                   + "FROM " + TABLE_LESSONS + " l "
                   + "JOIN " + TABLE_SUBJECTS + " s ON l.subject_id = s.id "
                   + "JOIN " + TABLE_GROUPS + " g ON l.group_id = g.id "
                   + "WHERE l.teacher_id = ? AND l.day_of_week = ? "
                   + "ORDER BY l.start_time";
        return db.rawQuery(sql, new String[]{teacherId, String.valueOf(dayOfWeek)});
    }

    
    public Cursor findTeacherScheduleForDayRaw(String teacherId, int dayOfWeek) {
        SQLiteDatabase db = getReadableDatabase();
        String dow = String.valueOf(dayOfWeek);
        String sql = "SELECT l.id, l.start_time, l.end_time, l.classroom, l.lesson_type, "
                + "'lesson' AS item_type, "
                + "s.name AS subject_name, g.code AS group_code, "
                + "l.group_id, l.subject_id, "
                + "'' AS label "
                + "FROM " + TABLE_LESSONS + " l "
                + "JOIN " + TABLE_SUBJECTS + " s ON l.subject_id = s.id "
                + "JOIN " + TABLE_GROUPS + " g ON l.group_id = g.id "
                + "WHERE l.teacher_id = ? AND l.day_of_week = ? "
                + "UNION ALL "
                + "SELECT b.id, b.start_time, b.end_time, '' AS classroom, '' AS lesson_type, "
                + "'break' AS item_type, "
                + "'' AS subject_name, '' AS group_code, "
                + "'' AS group_id, '' AS subject_id, "
                + "b.label "
                + "FROM " + TABLE_BREAKS + " b "
                + "WHERE (b.group_id IN (SELECT DISTINCT l2.group_id FROM "
                + TABLE_LESSONS + " l2 WHERE l2.teacher_id = ? AND l2.day_of_week = ?) "
                + "OR b.group_id IS NULL) "
                + "AND (b.day_of_week = ? OR b.day_of_week IS NULL) "
                + "ORDER BY start_time";
        return db.rawQuery(sql, new String[]{teacherId, dow, teacherId, dow, dow});
    }

    
    public Cursor findAssignmentsForTeacherWithStatsRaw(String teacherId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT a.id, a.title, a.due_date, g.code AS group_code, "
                   + "(SELECT COUNT(*) FROM " + TABLE_STUDENTS + " st WHERE st.group_id = a.group_id) AS total, "
                   + "SUM(CASE WHEN c.status IN ('SUBMITTED','COMPLETED') THEN 1 ELSE 0 END) AS submitted "
                   + "FROM " + TABLE_ASSIGNMENTS + " a "
                   + "JOIN " + TABLE_GROUPS + " g ON a.group_id = g.id "
                   + "LEFT JOIN " + TABLE_COMPLETIONS + " c ON c.assignment_id = a.id "
                   + "WHERE a.teacher_id = ? "
                   + "GROUP BY a.id ORDER BY a.due_date ASC";
        return db.rawQuery(sql, new String[]{teacherId});
    }

    
    public Cursor findAssignmentsForTeacherByDateRaw(String teacherId, String date) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT a.id, a.title, a.due_date, g.code AS group_code, g.id AS group_id, "
                   + "(SELECT COUNT(*) FROM " + TABLE_STUDENTS + " st WHERE st.group_id = a.group_id) AS total, "
                   + "SUM(CASE WHEN c.status IN ('SUBMITTED','COMPLETED') THEN 1 ELSE 0 END) AS submitted "
                   + "FROM " + TABLE_ASSIGNMENTS + " a "
                   + "JOIN " + TABLE_GROUPS + " g ON a.group_id = g.id "
                   + "LEFT JOIN " + TABLE_COMPLETIONS + " c ON c.assignment_id = a.id "
                   + "WHERE a.teacher_id = ? AND a." + COLUMN_ASSIGNED_DATE + " = ? "
                   + "GROUP BY a.id ORDER BY a.due_date ASC";
        return db.rawQuery(sql, new String[]{teacherId, date});
    }

    

    
    public Cursor findGroupsForTeacherRaw(String teacherId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT DISTINCT g.id, g.code, g.name, g.max_students, "
                   + "(SELECT COUNT(*) FROM " + TABLE_STUDENTS + " st WHERE st.group_id = g.id) AS students_count "
                   + "FROM " + TABLE_GROUPS + " g "
                   + "JOIN " + TABLE_LESSONS + " l ON l.group_id = g.id "
                   + "WHERE l.teacher_id = ? ORDER BY g.code";
        return db.rawQuery(sql, new String[]{teacherId});
    }

    
    public Cursor findSubjectsForTeacherRaw(String teacherId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT DISTINCT s.id, s.name, s.description "
                   + "FROM " + TABLE_SUBJECTS + " s "
                   + "JOIN " + TABLE_LESSONS + " l ON l.subject_id = s.id "
                   + "WHERE l.teacher_id = ? ORDER BY s.name";
        return db.rawQuery(sql, new String[]{teacherId});
    }

    

    
    public Cursor findStudentsInGroupRaw(String groupId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT st.id AS student_id, "
                   + "u.last_name || ' ' || SUBSTR(u.first_name,1,1) || '.' AS student_name "
                   + "FROM " + TABLE_STUDENTS + " st "
                   + "JOIN " + TABLE_USERS + " u ON st.user_id = u.id "
                   + "WHERE st.group_id = ? ORDER BY u.last_name";
        return db.rawQuery(sql, new String[]{groupId});
    }

    public List<String> findStudentUserIdsInGroup(String groupId) {
        List<String> ids = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT st.id FROM " + TABLE_STUDENTS + " st WHERE st.group_id = ?",
                new String[]{groupId});
        while (c.moveToNext()) {
            ids.add(c.getString(0));
        }
        c.close();
        return ids;
    }

    
    public Cursor findGradesForGroupSubjectRaw(String groupId, String subjectId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT g.id AS grade_id, g.student_id, g.value, g.grade_type, g.created_at, g.lesson_id "
                   + "FROM " + TABLE_GRADES + " g "
                   + "JOIN " + TABLE_STUDENTS + " st ON g.student_id = st.id "
                   + "WHERE st.group_id = ? AND g.subject_id = ? "
                   + "ORDER BY g.student_id, g.created_at";
        return db.rawQuery(sql, new String[]{groupId, subjectId});
    }

    public Cursor findGradesForGroupSubjectInPeriod(String groupId, String subjectId, String startDate, String endDate) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT g.id AS grade_id, g.student_id, g.value, g.grade_type, g.created_at "
                   + "FROM " + TABLE_GRADES + " g "
                   + "JOIN " + TABLE_STUDENTS + " st ON g.student_id = st.id "
                   + "WHERE st.group_id = ? AND g.subject_id = ? "
                   + "AND g.created_at >= ? AND g.created_at < ? "
                   + "ORDER BY g.student_id, g.created_at";
        return db.rawQuery(sql, new String[]{groupId, subjectId, startDate, endDate});
    }

    

    
    public Cursor findLessonByIdRaw(String lessonId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT l.id, l.day_of_week, l.start_time, l.end_time, l.classroom, l.lesson_type, l.group_id, l.subject_id, "
                + "s.name AS subject_name, g.code AS group_code, g.name AS group_name, l.lesson_topic "
                + "FROM " + TABLE_LESSONS + " l "
                + "JOIN " + TABLE_SUBJECTS + " s ON l.subject_id = s.id "
                + "JOIN " + TABLE_GROUPS + " g ON l.group_id = g.id "
                + "WHERE l.id = ?";
        return db.rawQuery(sql, new String[]{lessonId});
    }

    
    public boolean updateLessonTopic(String lessonId, String topic) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LESSON_TOPIC, topic);
        int rows = db.update(TABLE_LESSONS, values, COLUMN_LESSON_ID + " = ?", new String[]{lessonId});
        if (rows > 0 && context != null) {
            pushLessonUpdateIfPossible(lessonId);
        }
        return rows > 0;
    }

    private void pushLessonUpdateIfPossible(String lessonId) {
        if (context == null) return;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(TABLE_LESSONS, new String[]{
                            COLUMN_LESSON_GROUP_ID, COLUMN_LESSON_SUBJECT_ID, COLUMN_LESSON_TEACHER_ID,
                            COLUMN_DAY_OF_WEEK, COLUMN_START_TIME, COLUMN_END_TIME, COLUMN_CLASSROOM,
                            COLUMN_LESSON_TYPE, COLUMN_WEEK_NUMBER, COLUMN_IS_ALTERNATING, COLUMN_LESSON_TOPIC},
                    COLUMN_LESSON_ID + " = ?", new String[]{lessonId}, null, null, null);
            if (!c.moveToFirst()) return;
            Map<String, Object> body = new HashMap<>();
            body.put("groupId", safeApiInt(c.getString(0)));
            body.put("subjectId", safeApiInt(c.getString(1)));
            body.put("teacherId", resolveTeacherIdForApi(c.getString(2)));
            body.put("dayOfWeek", c.getInt(3));
            body.put("startTime", c.getString(4));
            body.put("endTime", c.getString(5));
            body.put("classroom", c.getString(6));
            body.put("lessonType", c.getString(7));
            body.put("weekNumber", c.isNull(8) ? 1 : c.getInt(8));
            body.put("isAlternatingWeek", c.getInt(9) == 1);
            body.put("lessonTopic", c.getString(10));
            ServerSyncManager.lessonUpdate(context, lessonId, body);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
    }

    
    public Cursor findAttendanceByLessonAndDateRaw(String lessonId, String date) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT a.id, a.student_id, a.status, a.comment "
                + "FROM " + TABLE_ATTENDANCE + " a "
                + "WHERE a.lesson_id = ? AND a.date = ?";
        return db.rawQuery(sql, new String[]{lessonId, date});
    }

    
    public Cursor findGradesByLessonRaw(String lessonId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT g.id, g.student_id, g.value, g.comment "
                + "FROM " + TABLE_GRADES + " g "
                + "WHERE g.lesson_id = ?";
        return db.rawQuery(sql, new String[]{lessonId});
    }

    
    public Cursor findGradesByLessonWithDetailsRaw(String lessonId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT g.id, g.student_id, g.value, g.grade_type, g.created_at, g.comment "
                + "FROM " + TABLE_GRADES + " g "
                + "WHERE g.lesson_id = ? "
                + "ORDER BY g.student_id, g.created_at";
        return db.rawQuery(sql, new String[]{lessonId});
    }

    
    public Cursor findGradesForGroupSubjectOnDateRaw(String groupId, String subjectId, String date) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT g.id, g.student_id, g.value, g.grade_type, g.created_at, g.comment "
                + "FROM " + TABLE_GRADES + " g "
                + "JOIN " + TABLE_STUDENTS + " st ON g.student_id = st.id "
                + "WHERE st.group_id = ? AND g.subject_id = ? "
                + "AND substr(g.created_at, 1, 10) = ? "
                + "ORDER BY g.student_id, g.created_at";
        return db.rawQuery(sql, new String[]{groupId, subjectId, date});
    }

    
    public int countGradesForLessonOnDate(String lessonId, String date) {
        if (lessonId == null || date == null) return 0;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + TABLE_GRADES + " g "
                + "WHERE g.lesson_id = ? AND substr(g.created_at, 1, 10) = ?";
        try (Cursor c = db.rawQuery(sql, new String[]{lessonId, date})) {
            if (c.moveToFirst()) return c.getInt(0);
        }
        return 0;
    }

    public int countGradesForGroupSubjectOnDate(String groupId, String subjectId, String date) {
        if (groupId == null || subjectId == null || date == null) return 0;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT COUNT(*) FROM " + TABLE_GRADES + " g "
                + "JOIN " + TABLE_STUDENTS + " st ON g.student_id = st.id "
                + "WHERE st.group_id = ? AND g.subject_id = ? AND substr(g.created_at, 1, 10) = ?";
        try (Cursor c = db.rawQuery(sql, new String[]{groupId, subjectId, date})) {
            if (c.moveToFirst()) return c.getInt(0);
        }
        return 0;
    }

    
    public int countGradesForLessonDay(String lessonId, String groupId, String subjectId, String date) {
        if (groupId == null || subjectId == null || date == null) {
            return countGradesForLessonOnDate(lessonId, date);
        }
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT COUNT(DISTINCT g.id) FROM " + TABLE_GRADES + " g "
                + "JOIN " + TABLE_STUDENTS + " st ON g.student_id = st.id "
                + "WHERE st.group_id = ? AND g.subject_id = ? "
                + "AND substr(g.created_at, 1, 10) = ? "
                + "AND (g.lesson_id = ? OR g.lesson_id IS NULL OR TRIM(g.lesson_id) = '')";
        try (Cursor c = db.rawQuery(sql, new String[]{groupId, subjectId, date, lessonId})) {
            if (c.moveToFirst()) return c.getInt(0);
        }
        return 0;
    }

    
    public String findFirstLessonIdForGroupSubjectDay(String groupId, String subjectId, int dayOfWeek) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE_LESSONS,
                new String[]{COLUMN_LESSON_ID},
                COLUMN_LESSON_GROUP_ID + " = ? AND " + COLUMN_LESSON_SUBJECT_ID + " = ? AND "
                        + COLUMN_DAY_OF_WEEK + " = ?",
                new String[]{groupId, subjectId, String.valueOf(dayOfWeek)},
                null, null,
                COLUMN_START_TIME + " ASC",
                "1")) {
            if (c.moveToFirst()) return c.getString(0);
        }
        return "";
    }

    
    public Cursor findLatestHomeworkForSubjectGroup(String subjectId, String groupId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT a.id, a.title, a.description, a.due_date, a.assigned_date, a.max_points "
                + "FROM " + TABLE_ASSIGNMENTS + " a "
                + "WHERE a.subject_id = ? AND a.group_id = ? AND a.assignment_type = 'HOMEWORK' "
                + "ORDER BY a.assigned_date DESC LIMIT 1";
        return db.rawQuery(sql, new String[]{subjectId, groupId});
    }

    public Cursor findHomeworkForSubjectGroupByDate(String subjectId, String groupId, String date) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT a.id, a.title, a.description, a.due_date, a.assigned_date, a.max_points "
                + "FROM " + TABLE_ASSIGNMENTS + " a "
                + "WHERE a.subject_id = ? AND a.group_id = ? AND a.assignment_type = 'HOMEWORK' "
                + "AND a.assigned_date = ? "
                + "ORDER BY a.assigned_date DESC LIMIT 1";
        return db.rawQuery(sql, new String[]{subjectId, groupId, date});
    }

    
    public long upsertAttendance(String lessonId, String date, String studentId,
                                          String status, String comment, String teacherId) {
        SQLiteDatabase db = getWritableDatabase();
        String resolvedTeacherId = resolveTeacherIdForStorage(teacherId);
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_ATTENDANCE_STATUS, status);
        values.put(COLUMN_ATTENDANCE_COMMENT, comment);
        values.put(COLUMN_ATTENDANCE_MARKED_BY, resolvedTeacherId);
        int rows = db.update(TABLE_ATTENDANCE, values,
                COLUMN_ATTENDANCE_STUDENT_ID + " = ? AND " + COLUMN_ATTENDANCE_LESSON_ID + " = ? AND " + COLUMN_ATTENDANCE_DATE + " = ?",
                new String[]{studentId, lessonId, date});
        if (rows > 0) {
            String existingId = findAttendanceId(studentId, lessonId, date);
            if (existingId != null && context != null) {
                Map<String, Object> body = new HashMap<>();
                body.put("studentId", safeApiInt(studentId));
                body.put("lessonId", safeApiInt(lessonId));
                body.put("date", date);
                body.put("status", status);
                body.put("comment", comment);
                body.put("markedBy", resolveTeacherIdForApi(teacherId));
                if (isServerNumericId(existingId)) {
                    ServerSyncManager.attendanceUpdate(context, existingId, body);
                } else {
                    ServerSyncManager.attendanceCreate(context, existingId, body);
                }
            }
            return rows;
        }
        
        String localId = UUID.randomUUID().toString();
        ContentValues insertValues = new ContentValues();
        insertValues.put(COLUMN_ATTENDANCE_ID, localId);
        insertValues.put(COLUMN_ATTENDANCE_STUDENT_ID, studentId);
        insertValues.put(COLUMN_ATTENDANCE_LESSON_ID, lessonId);
        insertValues.put(COLUMN_ATTENDANCE_DATE, date);
        insertValues.put(COLUMN_ATTENDANCE_STATUS, status);
        insertValues.put(COLUMN_ATTENDANCE_COMMENT, comment);
        insertValues.put(COLUMN_ATTENDANCE_MARKED_BY, resolvedTeacherId);
        long res = db.insert(TABLE_ATTENDANCE, null, insertValues);
        if (res != -1 && context != null) {
            Map<String, Object> body = new HashMap<>();
            body.put("studentId", safeApiInt(studentId));
            body.put("lessonId", safeApiInt(lessonId));
            body.put("date", date);
            body.put("status", status);
            body.put("comment", comment);
            body.put("markedBy", resolveTeacherIdForApi(teacherId));
            ServerSyncManager.attendanceCreate(context, localId, body);
        }
        return res;
    }

    private String findAttendanceId(String studentId, String lessonId, String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(TABLE_ATTENDANCE, new String[]{COLUMN_ATTENDANCE_ID},
                    COLUMN_ATTENDANCE_STUDENT_ID + " = ? AND " + COLUMN_ATTENDANCE_LESSON_ID + " = ? AND " + COLUMN_ATTENDANCE_DATE + " = ?",
                    new String[]{studentId, lessonId, date}, null, null, null);
            if (c.moveToFirst()) return c.getString(0);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    public Cursor findAttendanceForGroupInPeriod(String groupId, String startDate, String endDate) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT a.student_id, a.lesson_id, a.date, a.status "
                + "FROM " + TABLE_ATTENDANCE + " a "
                + "JOIN " + TABLE_STUDENTS + " st ON a.student_id = st.id "
                + "WHERE st.group_id = ? AND a.date >= ? AND a.date < ?";
        return db.rawQuery(sql, new String[]{groupId, startDate, endDate});
    }

    public int deleteAttendanceByLessonAndDate(String studentId, String lessonId, String date) {
        SQLiteDatabase db = getWritableDatabase();
        String existingId = findAttendanceId(studentId, lessonId, date);
        if (existingId != null) {
            ServerSyncManager.attendanceDelete(context, existingId);
        }
        return db.delete(TABLE_ATTENDANCE,
                COLUMN_ATTENDANCE_STUDENT_ID + " = ? AND " + COLUMN_ATTENDANCE_LESSON_ID + " = ? AND " + COLUMN_ATTENDANCE_DATE + " = ?",
                new String[]{studentId, lessonId, date});
    }

    
    public long insertGradeForLesson(String studentId, String subjectId, String teacherId,
                                              int value, String gradeType, String lessonId, String comment) {
        String localId = UUID.randomUUID().toString();
        String resolvedTeacherId = resolveTeacherIdForStorage(teacherId);
        ContentValues values = new ContentValues();
        values.put(COLUMN_GRADE_ID, localId);
        values.put(COLUMN_GRADE_STUDENT_ID, studentId);
        values.put(COLUMN_GRADE_SUBJECT_ID, subjectId);
        values.put(COLUMN_GRADE_TEACHER_ID, resolvedTeacherId);
        values.put(COLUMN_GRADE_VALUE, value);
        values.put(COLUMN_GRADE_TYPE, normalizeGradeTypeForStorage(gradeType));
        values.put(COLUMN_GRADE_LESSON_ID, lessonId);
        values.put(COLUMN_GRADE_COMMENT, comment);
        values.put(COLUMN_GRADE_WEIGHT, 1);
        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(TABLE_GRADES, null, values);
        if (result != -1) {
            if (context != null) {
                Map<String, Object> body = new HashMap<>();
                body.put("studentId", safeApiInt(studentId));
                body.put("subjectId", safeApiInt(subjectId));
                body.put("teacherId", resolveTeacherIdForApi(teacherId));
                body.put("value", value);
                body.put("gradeType", normalizeGradeTypeForStorage(gradeType));
                body.put("comment", comment != null ? comment : "");
                body.put("lessonId", safeApiInt(lessonId));
                body.put("weight", 1);
                ServerSyncManager.gradeCreate(context, localId, body);
            }
            String subjectName = findSubjectNameById(subjectId);
            if (subjectName == null) subjectName = "Предмет";
            insertNotification(studentId, "Новая оценка",
                    subjectName + ": " + formatGradeText(value, gradeType), "NEW_GRADE");
        }
        return result;
    }

    
    public long insertGradeForLesson(String studentId, String subjectId, String teacherId,
                                     int value, String gradeType, String comment, String lessonId, String fullDate) {
        String localId = UUID.randomUUID().toString();
        String resolvedTeacherId = resolveTeacherIdForStorage(teacherId);
        ContentValues values = new ContentValues();
        values.put(COLUMN_GRADE_ID, localId);
        values.put(COLUMN_GRADE_STUDENT_ID, studentId);
        values.put(COLUMN_GRADE_SUBJECT_ID, subjectId);
        values.put(COLUMN_GRADE_TEACHER_ID, resolvedTeacherId);
        values.put(COLUMN_GRADE_VALUE, value);
        values.put(COLUMN_GRADE_TYPE, normalizeGradeTypeForStorage(gradeType));
        values.put(COLUMN_GRADE_LESSON_ID, lessonId);
        values.put(COLUMN_GRADE_COMMENT, comment);
        values.put(COLUMN_GRADE_WEIGHT, 1);
        values.put(COLUMN_GRADE_CREATED, fullDate + " 00:00:00");
        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(TABLE_GRADES, null, values);
        if (result != -1) {
            if (context != null) {
                Map<String, Object> body = new HashMap<>();
                body.put("studentId", safeApiInt(studentId));
                body.put("subjectId", safeApiInt(subjectId));
                body.put("teacherId", resolveTeacherIdForApi(teacherId));
                body.put("value", value);
                body.put("gradeType", normalizeGradeTypeForStorage(gradeType));
                body.put("comment", comment != null ? comment : "");
                body.put("lessonId", safeApiInt(lessonId));
                body.put("weight", 1);
                body.put("createdAt", toIsoDateTime(fullDate + " 00:00:00"));
                ServerSyncManager.gradeCreate(context, localId, body);
            }
            String subjectName = findSubjectNameById(subjectId);
            if (subjectName == null) subjectName = "Предмет";
            insertNotification(studentId, "Новая оценка",
                    subjectName + ": " + formatGradeText(value, gradeType), "NEW_GRADE");
        }
        return result;
    }

    
    public int deleteGradeForLesson(String lessonId, String studentId) {
        SQLiteDatabase db = getWritableDatabase();
        
        
        if (context != null) {
            Cursor c = null;
            try {
                c = db.query(TABLE_GRADES, new String[]{COLUMN_GRADE_ID},
                        COLUMN_GRADE_LESSON_ID + " = ? AND " + COLUMN_GRADE_STUDENT_ID + " = ?",
                        new String[]{lessonId, studentId}, null, null, null);
                while (c.moveToNext()) {
                    ServerSyncManager.gradeDelete(context, c.getString(0));
                }
            } catch (Exception ignored) {
            } finally {
                if (c != null) c.close();
            }
        }
        return db.delete(TABLE_GRADES,
                COLUMN_GRADE_LESSON_ID + " = ? AND " + COLUMN_GRADE_STUDENT_ID + " = ?",
                new String[]{lessonId, studentId});
    }

    

    
    public Cursor findAssignmentByIdRaw(String assignmentId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT a.id, a.title, a.description, a.due_date, "
                   + "s." + COLUMN_SUBJECT_NAME + " AS subject_name, "
                   + "g." + COLUMN_GROUP_CODE + " AS group_code, "
                   + "a." + COLUMN_ASSIGNMENT_SUBJECT_ID + ", "
                   + "a." + COLUMN_ASSIGNMENT_GROUP_ID + " "
                   + "FROM " + TABLE_ASSIGNMENTS + " a "
                   + "JOIN " + TABLE_SUBJECTS + " s ON a." + COLUMN_ASSIGNMENT_SUBJECT_ID + " = s." + COLUMN_SUBJECT_ID + " "
                   + "JOIN " + TABLE_GROUPS + " g ON a." + COLUMN_ASSIGNMENT_GROUP_ID + " = g." + COLUMN_GROUP_ID + " "
                   + "WHERE a." + COLUMN_ASSIGNMENT_ID + " = ?";
        return db.rawQuery(sql, new String[]{assignmentId});
    }

    

    
    public String insertAssignment(String title,
                                   String description,
                                   String subjectId,
                                   String teacherId,
                                   String groupId,
                                   String assignedDate,
                                   String dueDate,
                                   String assignmentType,
                                   Integer maxPoints) {
        String localId = UUID.randomUUID().toString();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ASSIGNMENT_ID, localId);
        values.put(COLUMN_ASSIGNMENT_TITLE, title);
        values.put(COLUMN_ASSIGNMENT_DESC, description);
        values.put(COLUMN_ASSIGNMENT_SUBJECT_ID, subjectId);
        values.put(COLUMN_ASSIGNMENT_TEACHER_ID, teacherId);
        values.put(COLUMN_ASSIGNMENT_GROUP_ID, groupId);
        values.put(COLUMN_ASSIGNED_DATE, assignedDate);
        values.put(COLUMN_DUE_DATE, dueDate);
        values.put(COLUMN_ASSIGNMENT_TYPE, assignmentType != null ? assignmentType : "HOMEWORK");
        values.put(COLUMN_MAX_POINTS, maxPoints != null ? maxPoints : 10);

        SQLiteDatabase db = getWritableDatabase();
        long res = db.insert(TABLE_ASSIGNMENTS, null, values);
        if (res == -1) return null;

        if (context != null) {
            Map<String, Object> body = new HashMap<>();
            body.put("title", title);
            body.put("description", description);
            body.put("subjectId", subjectId);
            body.put("teacherId", teacherId);
            body.put("groupId", groupId);
            body.put("assignedDate", assignedDate);
            body.put("dueDate", dueDate);
            body.put("assignmentType", assignmentType != null ? assignmentType : "HOMEWORK");
            body.put("maxPoints", maxPoints != null ? maxPoints : 10);
            ServerSyncManager.assignmentCreate(context, localId, body);
        }
        return localId;
    }

    
    public boolean updateAssignmentDescription(String assignmentId, String description) {
        SQLiteDatabase db = getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(COLUMN_ASSIGNMENT_DESC, description);
        int rows = db.update(TABLE_ASSIGNMENTS, values,
                COLUMN_ASSIGNMENT_ID + " = ?", new String[]{assignmentId});
        if (rows > 0 && context != null) {
            pushAssignmentUpdateIfPossible(assignmentId);
        }
        return rows > 0;
    }

    
    public boolean updateAssignmentTitleAndDue(String assignmentId, String title, String dueDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        if (title != null) values.put(COLUMN_ASSIGNMENT_TITLE, title);
        if (dueDate != null) values.put(COLUMN_DUE_DATE, dueDate);
        if (values.size() == 0) return false;
        int rows = db.update(TABLE_ASSIGNMENTS, values,
                COLUMN_ASSIGNMENT_ID + " = ?", new String[]{assignmentId});
        if (rows > 0 && context != null) {
            pushAssignmentUpdateIfPossible(assignmentId);
        }
        return rows > 0;
    }

    
    public boolean deleteAssignmentById(String assignmentId) {
        if (context != null) {
            ServerSyncManager.assignmentDelete(context, assignmentId);
        }
        SQLiteDatabase db = getWritableDatabase();
        
        int rows = db.delete(TABLE_ASSIGNMENTS, COLUMN_ASSIGNMENT_ID + " = ?",
                new String[]{assignmentId});
        return rows > 0;
    }

    
    private void pushAssignmentUpdateIfPossible(String assignmentId) {
        if (context == null) return;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(TABLE_ASSIGNMENTS, new String[]{
                            COLUMN_ASSIGNMENT_TITLE, COLUMN_ASSIGNMENT_DESC, COLUMN_ASSIGNMENT_SUBJECT_ID,
                            COLUMN_ASSIGNMENT_TEACHER_ID, COLUMN_ASSIGNMENT_GROUP_ID, COLUMN_ASSIGNED_DATE,
                            COLUMN_DUE_DATE, COLUMN_ASSIGNMENT_TYPE, COLUMN_MAX_POINTS},
                    COLUMN_ASSIGNMENT_ID + " = ?", new String[]{assignmentId},
                    null, null, null);
            if (!c.moveToFirst()) return;
            Map<String, Object> body = new HashMap<>();
            body.put("title", c.getString(0));
            body.put("description", c.getString(1));
            body.put("subjectId", safeApiInt(c.getString(2)));
            body.put("teacherId", resolveTeacherIdForApi(c.getString(3)));
            body.put("groupId", safeApiInt(c.getString(4)));
            body.put("assignedDate", c.getString(5));
            body.put("dueDate", c.getString(6));
            body.put("assignmentType", c.getString(7) != null ? c.getString(7) : "HOMEWORK");
            body.put("maxPoints", c.isNull(8) ? 10 : c.getInt(8));
            ServerSyncManager.assignmentUpdate(context, assignmentId, body);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
    }

    

    
    public Cursor findSubmissionsForAssignmentRaw(String assignmentId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT u.last_name || ' ' || u.first_name AS student_name, "
                   + "c.status, c.submitted_at "
                   + "FROM " + TABLE_COMPLETIONS + " c "
                   + "JOIN " + TABLE_STUDENTS + " st ON c.student_id = st.id "
                   + "JOIN " + TABLE_USERS + " u ON st.user_id = u.id "
                   + "WHERE c.assignment_id = ? ORDER BY u.last_name";
        return db.rawQuery(sql, new String[]{assignmentId});
    }

    
    public Cursor findFirstAssignmentForTeacherRaw(String teacherId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT a.id, a.title, a.description, a.due_date "
                   + "FROM " + TABLE_ASSIGNMENTS + " a "
                   + "WHERE a.teacher_id = ? ORDER BY a.due_date ASC LIMIT 1";
        return db.rawQuery(sql, new String[]{teacherId});
    }

    
    public Cursor findAssignmentsForTeacherAndGroupRaw(String teacherId, String groupId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT a.id, a.title, a.description, a.due_date, g.code AS group_code, "
                   + "s.name AS subject_name, "
                   + "(SELECT COUNT(*) FROM " + TABLE_STUDENTS + " st WHERE st.group_id = a.group_id) AS total, "
                   + "SUM(CASE WHEN c.status IN ('SUBMITTED','COMPLETED') THEN 1 ELSE 0 END) AS submitted, "
                   + "SUM(CASE WHEN c.status = 'NOT_STARTED' AND a.due_date < date('now') THEN 1 ELSE 0 END) AS overdue_count, "
                   + "SUM(CASE WHEN c.status = 'SUBMITTED' THEN 1 ELSE 0 END) AS on_review "
                   + "FROM " + TABLE_ASSIGNMENTS + " a "
                   + "JOIN " + TABLE_GROUPS + " g ON a.group_id = g.id "
                   + "JOIN " + TABLE_SUBJECTS + " s ON a.subject_id = s.id "
                   + "LEFT JOIN " + TABLE_COMPLETIONS + " c ON c.assignment_id = a.id "
                   + "WHERE a.teacher_id = ? AND a.group_id = ? "
                   + "GROUP BY a.id ORDER BY a.due_date DESC";
        return db.rawQuery(sql, new String[]{teacherId, groupId});
    }

    
    public Cursor findSubmissionsForAssignmentWithDetailsRaw(String assignmentId, String groupId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT c.id AS completion_id, st.id AS student_id, "
                   + "COALESCE(c.status, 'NOT_STARTED') AS status, "
                   + "c.submitted_at, c.received_points, c.student_comment, c.graded_at, "
                   + "COALESCE(u.last_name, '—') || ' ' || COALESCE(u.first_name, '') AS student_name "
                   + "FROM " + TABLE_ASSIGNMENTS + " a "
                   + "JOIN " + TABLE_STUDENTS + " st ON st.group_id = ? "
                   + "LEFT JOIN " + TABLE_USERS + " u ON st.user_id = u.id "
                   + "LEFT JOIN " + TABLE_COMPLETIONS + " c ON c.assignment_id = a.id AND c.student_id = st.id "
                   + "WHERE a.id = ? "
                   + "ORDER BY COALESCE(u.last_name, '')";
        return db.rawQuery(sql, new String[]{groupId, assignmentId});
    }

    
    public boolean gradeHomeworkCompletion(String completionId, String status, Integer receivedPoints, String comment) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLETION_STATUS, status);
        if (receivedPoints != null) {
            values.put(COLUMN_RECEIVED_POINTS, receivedPoints);
        }
        if (comment != null) {
            values.put(COLUMN_STUDENT_COMMENT, comment);
        }
        if ("COMPLETED".equals(status)) {
            values.put(COLUMN_GRADED_AT, nowISO());
        }
        int rows = db.update(TABLE_COMPLETIONS, values,
                COLUMN_COMPLETION_ID + " = ?", new String[]{completionId});
        if (rows > 0 && context != null) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", status);
            body.put("receivedPoints", receivedPoints);
            body.put("studentComment", comment);
            body.put("gradedAt", "COMPLETED".equals(status) ? nowISO() : null);
            ServerSyncManager.homeworkCompletionUpdate(context, completionId, body);
        }
        return rows > 0;
    }

    
    public String findLessonIdByAssignment(String assignmentId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT a." + COLUMN_ASSIGNMENT_SUBJECT_ID + ", a." + COLUMN_ASSIGNMENT_TEACHER_ID
                + ", a." + COLUMN_ASSIGNMENT_GROUP_ID + ", a." + COLUMN_DUE_DATE
                + " FROM " + TABLE_ASSIGNMENTS + " a WHERE a." + COLUMN_ASSIGNMENT_ID + " = ?";
        Cursor c = db.rawQuery(sql, new String[]{assignmentId});
        String lessonId = null;
        if (c.moveToFirst()) {
            String subjectId = c.getString(0);
            String teacherId = c.getString(1);
            String groupId = c.getString(2);
            String dueDate = c.getString(3);
            
            String lessonSql = "SELECT l." + COLUMN_LESSON_ID
                    + " FROM " + TABLE_LESSONS + " l"
                    + " WHERE l." + COLUMN_LESSON_GROUP_ID + " = ?"
                    + " AND l." + COLUMN_LESSON_SUBJECT_ID + " = ?"
                    + " AND l." + COLUMN_LESSON_TEACHER_ID + " = ?"
                    + " LIMIT 1";
            Cursor lc = db.rawQuery(lessonSql, new String[]{groupId, subjectId, teacherId});
            if (lc.moveToFirst()) {
                lessonId = lc.getString(0);
            }
            lc.close();
        }
        c.close();
        return lessonId;
    }

    
    public boolean gradeHomeworkWithGrade(String completionId, String studentId, String assignmentId,
                                           int gradeValue, String comment) {
        
        boolean ok = gradeHomeworkCompletion(completionId, "COMPLETED", gradeValue, comment);
        if (!ok) return false;

        
        String lessonId = findLessonIdByAssignment(assignmentId);
        if (lessonId == null) return ok;

        
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + COLUMN_ASSIGNMENT_SUBJECT_ID + ", " + COLUMN_ASSIGNMENT_TEACHER_ID
                + " FROM " + TABLE_ASSIGNMENTS + " WHERE " + COLUMN_ASSIGNMENT_ID + " = ?";
        Cursor c = db.rawQuery(sql, new String[]{assignmentId});
        if (c.moveToFirst()) {
            String subjectId = c.getString(0);
            String teacherId = c.getString(1);
            
            deleteGradeForLesson(lessonId, studentId);
            insertGradeForLesson(studentId, subjectId, teacherId, gradeValue, "HOMEWORK", lessonId, comment);
        }
        c.close();
        return true;
    }

    
    public void createCompletionsForAssignment(String assignmentId, String groupId) {
        createHomeworkCompletions(assignmentId, groupId);
    }

    

    
    public Cursor findStudentsByGroup(String groupId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT st.id AS student_id, "
                + "u.last_name || ' ' || u.first_name || ' ' || COALESCE(u.patronymic, '') AS student_name, "
                + "u.phone_number, g.code AS group_code "
                + "FROM " + TABLE_STUDENTS + " st "
                + "JOIN " + TABLE_USERS + " u ON st.user_id = u.id "
                + "JOIN " + TABLE_GROUPS + " g ON st.group_id = g.id "
                + "WHERE st.group_id = ? ORDER BY u.last_name";
        return db.rawQuery(sql, new String[]{groupId});
    }

    
    public Cursor findGradesByStudent(String studentId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT g.id AS grade_id, g.value, g.grade_type, g.created_at, g.comment, "
                + "COALESCE(s.name, '—') AS subject_name "
                + "FROM " + TABLE_GRADES + " g "
                + "LEFT JOIN " + TABLE_SUBJECTS + " s ON g.subject_id = s.id "
                + "WHERE g.student_id = ? "
                + "ORDER BY g.created_at DESC";
        return db.rawQuery(sql, new String[]{studentId});
    }

    
    public Cursor findAttendanceByStudent(String studentId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT a.id AS att_id, a.date, a.status, a.comment, "
                + "l.lesson_topic, l.start_time, l.end_time, COALESCE(s.name, '—') AS subject_name "
                + "FROM " + TABLE_ATTENDANCE + " a "
                + "LEFT JOIN " + TABLE_LESSONS + " l ON a.lesson_id = l.id "
                + "LEFT JOIN " + TABLE_SUBJECTS + " s ON l.subject_id = s.id "
                + "WHERE a.student_id = ? "
                + "ORDER BY a.date DESC";
        return db.rawQuery(sql, new String[]{studentId});
    }

    
    public Cursor findLessonByGroupSubjectTeacher(String groupId, String subjectId, String teacherId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT l.id, l.lesson_topic, l.start_time, l.end_time, l.classroom, l.lesson_type "
                + "FROM " + TABLE_LESSONS + " l "
                + "WHERE l.group_id = ? AND l.subject_id = ? AND l.teacher_id = ? "
                + "LIMIT 1";
        return db.rawQuery(sql, new String[]{groupId, subjectId, teacherId});
    }

    
    public Cursor findLessonsByGroupSubjectTeacher(String groupId, String subjectId, String teacherId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT l.id, l.day_of_week "
                + "FROM " + TABLE_LESSONS + " l "
                + "WHERE l.group_id = ? AND l.subject_id = ? AND l.teacher_id = ? "
                + "ORDER BY l.day_of_week";
        return db.rawQuery(sql, new String[]{groupId, subjectId, teacherId});
    }

    
    public Cursor findLessonsForGroupSubject(String groupId, String subjectId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT l.id, l.day_of_week, l.start_time, l.end_time, l.classroom, "
                + "l.lesson_type, l.lesson_topic, l.week_number, g.code AS group_code "
                + "FROM " + TABLE_LESSONS + " l "
                + "JOIN " + TABLE_GROUPS + " g ON l.group_id = g.id "
                + "WHERE l.group_id = ? AND l.subject_id = ? "
                + "ORDER BY l.day_of_week, l.start_time";
        return db.rawQuery(sql, new String[]{groupId, subjectId});
    }

    
    public Cursor findAllLessonsForSubjectRaw(String subjectId) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT l.id, l.group_id, l.day_of_week, l.start_time, l.end_time, l.classroom, "
                + "l.lesson_type, l.lesson_topic, l.week_number, g.code AS group_code "
                + "FROM " + TABLE_LESSONS + " l "
                + "JOIN " + TABLE_GROUPS + " g ON l.group_id = g.id "
                + "WHERE l.subject_id = ? "
                + "ORDER BY g.code, l.day_of_week, l.start_time";
        return db.rawQuery(sql, new String[]{subjectId});
    }

    

    
    public Cursor findGradeByStudentSubjectDate(String studentId, String subjectId, String date) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT id, value, lesson_id FROM " + TABLE_GRADES
                + " WHERE " + COLUMN_GRADE_STUDENT_ID + " = ? AND " + COLUMN_GRADE_SUBJECT_ID + " = ? "
                + " AND DATE(" + COLUMN_GRADE_CREATED + ") = DATE(?) LIMIT 1";
        return db.rawQuery(sql, new String[]{studentId, subjectId, date});
    }

    
    public long insertGradeSimple(String studentId, String subjectId, String teacherId,
                                   int value, String gradeType, String comment) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_GRADE_ID, UUID.randomUUID().toString());
        values.put(COLUMN_GRADE_STUDENT_ID, studentId);
        values.put(COLUMN_GRADE_SUBJECT_ID, subjectId);
        values.put(COLUMN_GRADE_TEACHER_ID, teacherId);
        values.put(COLUMN_GRADE_VALUE, value);
        values.put(COLUMN_GRADE_TYPE, gradeType);
        values.put(COLUMN_GRADE_COMMENT, comment);
        values.put(COLUMN_GRADE_WEIGHT, 1);
        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(TABLE_GRADES, null, values);
        if (result != -1) {
            String subjectName = findSubjectNameById(subjectId);
            if (subjectName == null) subjectName = "Предмет";
            insertNotification(studentId, "Новая оценка",
                    subjectName + ": " + formatGradeText(value, gradeType), "NEW_GRADE");
        }
        return result;
    }

    private String formatGradeText(int value, String gradeType) {
        if (gradeType == null) return String.valueOf(value);
        switch (gradeType) {
            case "ABSENT": return "Н (пропуск)";
            case "EXEMPT": return "Б (уважительная причина)";
            case "CREDIT_PASS": return "Зач";
            case "CONTROL": return value + " (Контрольная)";
            case "TEST": return value + " (Тест)";
            case "PRACTICAL": return value + " (Практическая)";
            case "EXAM": return value + " (Экзамен)";
            case "HOMEWORK": return value + " (Домашнее задание)";
            case "ESSAY": return value + " (Сочинение)";
            case "TEST_PREP": return value + " (Подготовка к тесту)";
            case "LAB": return value + " (Лабораторная)";
            default: return String.valueOf(value);
        }
    }

    
    public int deleteGradeById(String gradeId) {
        SQLiteDatabase db = getWritableDatabase();
        if (context != null) {
            ServerSyncManager.gradeDelete(context, gradeId);
        }
        return db.delete(TABLE_GRADES, COLUMN_GRADE_ID + " = ?", new String[]{gradeId});
    }

    
    public int updateGradeValue(String gradeId, int newValue) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_GRADE_VALUE, newValue);
        SQLiteDatabase db = getWritableDatabase();
        int res = db.update(TABLE_GRADES, values, COLUMN_GRADE_ID + " = ?", new String[]{gradeId});
        if (res > 0) pushGradeUpdateIfPossible(gradeId);
        return res;
    }

    public int updateGradeType(String gradeId, String gradeType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_GRADE_TYPE, gradeType);
        if (gradeType.equals("ABSENT") || gradeType.equals("EXEMPT") || gradeType.equals("CREDIT_PASS")) {
            values.put(COLUMN_GRADE_VALUE, 0);
        }
        SQLiteDatabase db = getWritableDatabase();
        int res = db.update(TABLE_GRADES, values, COLUMN_GRADE_ID + " = ?", new String[]{gradeId});
        if (res > 0) pushGradeUpdateIfPossible(gradeId);
        return res;
    }

    private void pushGradeUpdateIfPossible(String gradeId) {
        if (context == null) return;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(TABLE_GRADES,
                    new String[]{COLUMN_GRADE_STUDENT_ID, COLUMN_GRADE_SUBJECT_ID, COLUMN_GRADE_TEACHER_ID,
                            COLUMN_GRADE_VALUE, COLUMN_GRADE_TYPE, COLUMN_GRADE_COMMENT, COLUMN_GRADE_LESSON_ID,
                            COLUMN_GRADE_WEIGHT, COLUMN_GRADE_CREATED},
                    COLUMN_GRADE_ID + " = ?",
                    new String[]{gradeId},
                    null, null, null);
            if (!c.moveToFirst()) return;
            Map<String, Object> body = new HashMap<>();
            body.put("studentId", safeApiInt(c.getString(0)));
            body.put("subjectId", safeApiInt(c.getString(1)));
            
            body.put("teacherId", resolveTeacherIdForApi(c.getString(2)));
            body.put("value", c.getInt(3));
            body.put("gradeType", c.getString(4));
            String comment = c.getString(5);
            body.put("comment", comment != null ? comment : "");
            body.put("lessonId", safeApiInt(c.getString(6)));
            body.put("weight", c.isNull(7) ? 1 : c.getInt(7));
            body.put("createdAt", toIsoDateTime(c.getString(8)));
            ServerSyncManager.gradeUpdate(context, gradeId, body);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
    }

    private Integer safeApiInt(String id) {
        if (id == null) return null;
        String norm = com.example.eduhub.network.util.ApiMapUtils.normalizeId(id);
        try {
            return Integer.parseInt(norm);
        } catch (Exception e) {
            return null;
        }
    }

    
    private Integer resolveTeacherIdForApi(String teacherIdOrUserId) {
        String resolved = resolveTeacherIdForStorage(teacherIdOrUserId);
        return safeApiInt(resolved);
    }

    
    private String resolveUserIdForStudent(String studentId) {
        if (studentId == null || studentId.isEmpty()) return null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT user_id FROM " + TABLE_STUDENTS + " WHERE id = ? LIMIT 1",
                    new String[]{studentId});
            if (c.moveToFirst()) return c.getString(0);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    private boolean isServerNumericId(String id) {
        if (id == null || id.isEmpty()) return false;
        try {
            Integer.parseInt(com.example.eduhub.network.util.ApiMapUtils.normalizeId(id));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    
    private String resolveTeacherIdForStorage(String teacherIdOrUserId) {
        if (teacherIdOrUserId == null || teacherIdOrUserId.isEmpty()) return teacherIdOrUserId;
        
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT id FROM " + TABLE_TEACHERS + " WHERE id = ? LIMIT 1",
                    new String[]{teacherIdOrUserId});
            if (c.moveToFirst()) return teacherIdOrUserId;
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        
        try {
            c = db.rawQuery("SELECT id FROM " + TABLE_TEACHERS + " WHERE user_id = ? LIMIT 1",
                    new String[]{teacherIdOrUserId});
            if (c.moveToFirst()) return c.getString(0);
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return teacherIdOrUserId;
    }

    public long insertRow(String tableName, ContentValues values) {
        if (TABLE_GRADES.equals(tableName) && values.containsKey(COLUMN_GRADE_TYPE)) {
            values.put(COLUMN_GRADE_TYPE,
                    normalizeGradeTypeForStorage(values.getAsString(COLUMN_GRADE_TYPE)));
        }
        SQLiteDatabase db = getWritableDatabase();
        return db.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public long upsertUserFromSync(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        String userId = values.getAsString(COLUMN_USER_ID);
        if (userId == null) {
            return -1;
        }

        String email = values.getAsString(COLUMN_EMAIL);
        values.remove(COLUMN_PASSWORD_HASH);

        String incomingRole = values.getAsString(COLUMN_ROLE);
        if (incomingRole == null || incomingRole.trim().isEmpty()) {
            values.remove(COLUMN_ROLE);
        }

        if (email != null && !email.isEmpty()) {
            Cursor byEmail = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID},
                    COLUMN_EMAIL + " = ?", new String[]{email},
                    null, null, null);
            if (byEmail.moveToFirst()) {
                String existingId = byEmail.getString(0);
                byEmail.close();
                if (!userId.equals(existingId)) {
                    reassignUserPrimaryKey(db, existingId, userId);
                }
                return db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{userId});
            }
            byEmail.close();
        }

        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID},
                COLUMN_USER_ID + " = ?", new String[]{userId},
                null, null, null);
        boolean existsById = cursor.moveToFirst();
        cursor.close();

        if (existsById) {
            return db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{userId});
        }

        values.put(COLUMN_PASSWORD_HASH, PasswordHasher.hash(UUID.randomUUID().toString()));
        return db.insert(TABLE_USERS, null, values);
    }

    
    private void reassignUserPrimaryKey(SQLiteDatabase db, String oldId, String newId) {
        ContentValues fk = new ContentValues();
        fk.put(COLUMN_USER_ID_FK, newId);
        db.update(TABLE_STUDENTS, fk, COLUMN_USER_ID_FK + " = ?", new String[]{oldId});
        db.update(TABLE_TEACHERS, fk, COLUMN_USER_ID_FK + " = ?", new String[]{oldId});
        ContentValues idCv = new ContentValues();
        idCv.put(COLUMN_USER_ID, newId);
        db.update(TABLE_USERS, idCv, COLUMN_USER_ID + " = ?", new String[]{oldId});
    }

    
    public static int normalizeGroupCourseNumber(Integer apiCourseNumber) {
        if (apiCourseNumber == null) {
            return 1;
        }
        int n = apiCourseNumber;
        if (n >= 7 && n <= 12) {
            return Math.min(5, n - 6);
        }
        if (n < 1) {
            return 1;
        }
        return Math.min(12, n);
    }

    public String getLearningSystem(String userId) {
        if (sharedPreferences == null) return "semester";
        return sharedPreferences.getString("learning_system_" + learningKeyId(userId), "semester");
    }

    public void setLearningSystem(String userId, String system) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString("learning_system_" + learningKeyId(userId), system).apply();
        }
    }

    
    private String learningKeyId(String passedId) {
        if (context != null) {
            try {
                String sessionUserId = com.example.eduhub.network.session.UserSessionManager
                        .getInstance(context).getUserId();
                if (sessionUserId != null && !sessionUserId.isEmpty()) {
                    return sessionUserId;
                }
            } catch (Exception ignored) {
            }
        }
        return passedId;
    }

    public Cursor findLessonDaysForGroupSubject(String groupId, String subjectId) {
        SQLiteDatabase db = getReadableDatabase();
        
        
        return db.query(
                TABLE_LESSONS,
                new String[]{COLUMN_LESSON_ID + " AS lesson_id", COLUMN_DAY_OF_WEEK},
                COLUMN_LESSON_GROUP_ID + " = ? AND " + COLUMN_LESSON_SUBJECT_ID + " = ?",
                new String[]{groupId, subjectId},
                null, null,
                COLUMN_DAY_OF_WEEK + " ASC, " + COLUMN_START_TIME + " ASC");
    }

    public Cursor findHomeworkForSubjectGroupByDueDate(String subjectId, String groupId, String dueDate) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_ASSIGNMENTS, null,
                COLUMN_ASSIGNMENT_SUBJECT_ID + " = ? AND " + COLUMN_ASSIGNMENT_GROUP_ID + " = ? AND " + COLUMN_DUE_DATE + " = ?",
                new String[]{subjectId, groupId, dueDate},
                null, null, null);
    }
}



