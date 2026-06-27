package com.example.eduhub.database.schema;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.security.PasswordHasher;

public final class DatabaseCreator {

    private DatabaseCreator() {}

    public static void createSchema(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DBHelper.TABLE_USERS + " (" +
                DBHelper.COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
                DBHelper.COLUMN_PASSWORD_HASH + " TEXT NOT NULL, " +
                DBHelper.COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                DBHelper.COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                DBHelper.COLUMN_PATRONYMIC + " TEXT, " +
                DBHelper.COLUMN_PHONE + " TEXT, " +
                DBHelper.COLUMN_ROLE + " TEXT NOT NULL, " +
                DBHelper.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                DBHelper.COLUMN_LAST_LOGIN + " DATETIME, " +
                DBHelper.COLUMN_IS_ACTIVE + " INTEGER DEFAULT 1 CHECK(" + DBHelper.COLUMN_IS_ACTIVE + " IN (0, 1)), " +
                DBHelper.COLUMN_AVATAR + " TEXT);");

        db.execSQL("CREATE TABLE " + DBHelper.TABLE_STUDENTS + " (" +
                DBHelper.COLUMN_STUDENT_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_USER_ID_FK + " TEXT NOT NULL, " +
                DBHelper.COLUMN_STUDENT_GROUP_ID + " TEXT, " +
                DBHelper.COLUMN_ENROLLMENT_DATE + " DATE, " +
                DBHelper.COLUMN_STUDENT_NUMBER + " TEXT UNIQUE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_USER_ID_FK + ") REFERENCES " + DBHelper.TABLE_USERS + "(" + DBHelper.COLUMN_USER_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_STUDENT_GROUP_ID + ") REFERENCES " + DBHelper.TABLE_GROUPS + "(" + DBHelper.COLUMN_GROUP_ID + ") ON DELETE SET NULL);");

        db.execSQL("CREATE TABLE " + DBHelper.TABLE_TEACHERS + " (" +
                DBHelper.COLUMN_TEACHER_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_USER_ID_FK + " TEXT NOT NULL, " +
                DBHelper.COLUMN_EMPLOYEE_ID + " TEXT UNIQUE, " +
                DBHelper.COLUMN_DEPARTMENT + " TEXT, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_USER_ID_FK + ") REFERENCES " + DBHelper.TABLE_USERS + "(" + DBHelper.COLUMN_USER_ID + ") ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE " + DBHelper.TABLE_GROUPS + " (" +
                DBHelper.COLUMN_GROUP_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_GROUP_CODE + " TEXT UNIQUE NOT NULL, " +
                DBHelper.COLUMN_GROUP_NAME + " TEXT NOT NULL, " +
                DBHelper.COLUMN_COURSE_NUMBER + " INTEGER NOT NULL CHECK(" + DBHelper.COLUMN_COURSE_NUMBER + " BETWEEN 1 AND 12), " +
                DBHelper.COLUMN_SPECIALIZATION + " TEXT, " +
                DBHelper.COLUMN_MAX_STUDENTS + " INTEGER DEFAULT 30, " +
                DBHelper.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP);");

        db.execSQL("CREATE TABLE " + DBHelper.TABLE_SUBJECTS + " (" +
                DBHelper.COLUMN_SUBJECT_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_SUBJECT_CODE + " TEXT UNIQUE NOT NULL, " +
                DBHelper.COLUMN_SUBJECT_NAME + " TEXT NOT NULL, " +
                DBHelper.COLUMN_DESCRIPTION + " TEXT, " +
                DBHelper.COLUMN_TOTAL_HOURS + " INTEGER DEFAULT 0, " +
                DBHelper.COLUMN_CREDITS + " INTEGER DEFAULT 0, " +
                DBHelper.COLUMN_IS_EXAM + " INTEGER DEFAULT 0 CHECK(" + DBHelper.COLUMN_IS_EXAM + " IN (0, 1)), " +
                DBHelper.COLUMN_IS_CREDIT + " INTEGER DEFAULT 0 CHECK(" + DBHelper.COLUMN_IS_CREDIT + " IN (0, 1)));");

        db.execSQL("CREATE TABLE " + DBHelper.TABLE_GRADES + " (" +
                DBHelper.COLUMN_GRADE_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_GRADE_STUDENT_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_GRADE_SUBJECT_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_GRADE_TEACHER_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_GRADE_VALUE + " INTEGER NOT NULL DEFAULT 0, " +
                DBHelper.COLUMN_GRADE_TYPE + " TEXT NOT NULL DEFAULT 'CURRENT', " +
                DBHelper.COLUMN_GRADE_COMMENT + " TEXT, " +
                DBHelper.COLUMN_GRADE_LESSON_ID + " TEXT, " +
                DBHelper.COLUMN_GRADE_WEIGHT + " INTEGER DEFAULT 1, " +
                DBHelper.COLUMN_GRADE_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                DBHelper.COLUMN_GRADE_UPDATED + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                DBHelper.COLUMN_GRADE_UPDATED_BY + " TEXT, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_GRADE_STUDENT_ID + ") REFERENCES " + DBHelper.TABLE_STUDENTS + "(" + DBHelper.COLUMN_STUDENT_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_GRADE_SUBJECT_ID + ") REFERENCES " + DBHelper.TABLE_SUBJECTS + "(" + DBHelper.COLUMN_SUBJECT_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_GRADE_TEACHER_ID + ") REFERENCES " + DBHelper.TABLE_TEACHERS + "(" + DBHelper.COLUMN_TEACHER_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_GRADE_UPDATED_BY + ") REFERENCES " + DBHelper.TABLE_TEACHERS + "(" + DBHelper.COLUMN_TEACHER_ID + ") ON DELETE SET NULL);");

        db.execSQL("CREATE TABLE " + DBHelper.TABLE_ATTENDANCE + " (" +
                DBHelper.COLUMN_ATTENDANCE_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_ATTENDANCE_STUDENT_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_ATTENDANCE_LESSON_ID + " TEXT, " +
                DBHelper.COLUMN_ATTENDANCE_DATE + " DATE NOT NULL, " +
                DBHelper.COLUMN_ATTENDANCE_STATUS + " TEXT NOT NULL CHECK(" + DBHelper.COLUMN_ATTENDANCE_STATUS + " IN ('PRESENT', 'ABSENT', 'LATE', 'EXCUSED_ABSENT', 'EARLY_LEAVE')), " +
                DBHelper.COLUMN_ATTENDANCE_COMMENT + " TEXT, " +
                DBHelper.COLUMN_ATTENDANCE_MARKED + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                DBHelper.COLUMN_ATTENDANCE_MARKED_BY + " TEXT, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_ATTENDANCE_STUDENT_ID + ") REFERENCES " + DBHelper.TABLE_STUDENTS + "(" + DBHelper.COLUMN_STUDENT_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_ATTENDANCE_MARKED_BY + ") REFERENCES " + DBHelper.TABLE_TEACHERS + "(" + DBHelper.COLUMN_TEACHER_ID + ") ON DELETE SET NULL, " +
                "UNIQUE(" + DBHelper.COLUMN_ATTENDANCE_STUDENT_ID + ", " + DBHelper.COLUMN_ATTENDANCE_DATE + ", " + DBHelper.COLUMN_ATTENDANCE_LESSON_ID + "));");

        db.execSQL("CREATE TABLE " + DBHelper.TABLE_LESSONS + " (" +
                DBHelper.COLUMN_LESSON_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_LESSON_GROUP_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_LESSON_SUBJECT_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_LESSON_TEACHER_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_DAY_OF_WEEK + " INTEGER NOT NULL CHECK(" + DBHelper.COLUMN_DAY_OF_WEEK + " BETWEEN 1 AND 7), " +
                DBHelper.COLUMN_START_TIME + " TIME NOT NULL, " +
                DBHelper.COLUMN_END_TIME + " TIME NOT NULL, " +
                DBHelper.COLUMN_CLASSROOM + " TEXT, " +
                DBHelper.COLUMN_LESSON_TYPE + " TEXT CHECK(" + DBHelper.COLUMN_LESSON_TYPE + " IN ('LECTURE', 'PRACTICAL', 'LABORATORY', 'SEMINAR', 'CONSULTATION', 'EXAM', 'CREDIT')), " +
                DBHelper.COLUMN_WEEK_NUMBER + " INTEGER DEFAULT 1 CHECK(" + DBHelper.COLUMN_WEEK_NUMBER + " BETWEEN 1 AND 18), " +
                DBHelper.COLUMN_IS_ALTERNATING + " INTEGER DEFAULT 0 CHECK(" + DBHelper.COLUMN_IS_ALTERNATING + " IN (0, 1)), " +
                DBHelper.COLUMN_LESSON_TOPIC + " TEXT, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_LESSON_GROUP_ID + ") REFERENCES " + DBHelper.TABLE_GROUPS + "(" + DBHelper.COLUMN_GROUP_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_LESSON_SUBJECT_ID + ") REFERENCES " + DBHelper.TABLE_SUBJECTS + "(" + DBHelper.COLUMN_SUBJECT_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_LESSON_TEACHER_ID + ") REFERENCES " + DBHelper.TABLE_TEACHERS + "(" + DBHelper.COLUMN_TEACHER_ID + ") ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE " + DBHelper.TABLE_ASSIGNMENTS + " (" +
                DBHelper.COLUMN_ASSIGNMENT_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_ASSIGNMENT_TITLE + " TEXT NOT NULL, " +
                DBHelper.COLUMN_ASSIGNMENT_DESC + " TEXT, " +
                DBHelper.COLUMN_ASSIGNMENT_SUBJECT_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_ASSIGNMENT_TEACHER_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_ASSIGNMENT_GROUP_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_ASSIGNED_DATE + " DATE NOT NULL DEFAULT (date('now')), " +
                DBHelper.COLUMN_DUE_DATE + " DATE, " +
                DBHelper.COLUMN_ASSIGNMENT_TYPE + " TEXT NOT NULL CHECK(" + DBHelper.COLUMN_ASSIGNMENT_TYPE + " IN ('HOMEWORK', 'PRACTICAL', 'LABORATORY', 'ESSAY', 'PROJECT', 'PREPARATION')), " +
                DBHelper.COLUMN_ATTACHMENTS + " TEXT, " +
                DBHelper.COLUMN_MAX_POINTS + " INTEGER, " +
                DBHelper.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_ASSIGNMENT_SUBJECT_ID + ") REFERENCES " + DBHelper.TABLE_SUBJECTS + "(" + DBHelper.COLUMN_SUBJECT_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_ASSIGNMENT_TEACHER_ID + ") REFERENCES " + DBHelper.TABLE_TEACHERS + "(" + DBHelper.COLUMN_TEACHER_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_ASSIGNMENT_GROUP_ID + ") REFERENCES " + DBHelper.TABLE_GROUPS + "(" + DBHelper.COLUMN_GROUP_ID + ") ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE " + DBHelper.TABLE_COMPLETIONS + " (" +
                DBHelper.COLUMN_COMPLETION_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_COMPLETION_ASSIGNMENT_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_COMPLETION_STUDENT_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_COMPLETION_STATUS + " TEXT NOT NULL CHECK(" + DBHelper.COLUMN_COMPLETION_STATUS + " IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'SUBMITTED')), " +
                DBHelper.COLUMN_STUDENT_COMMENT + " TEXT, " +
                DBHelper.COLUMN_ATTACHMENTS + " TEXT, " +
                DBHelper.COLUMN_SUBMITTED_AT + " DATETIME, " +
                DBHelper.COLUMN_RECEIVED_POINTS + " INTEGER, " +
                DBHelper.COLUMN_GRADED_AT + " DATETIME, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_COMPLETION_ASSIGNMENT_ID + ") REFERENCES " + DBHelper.TABLE_ASSIGNMENTS + "(" + DBHelper.COLUMN_ASSIGNMENT_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_COMPLETION_STUDENT_ID + ") REFERENCES " + DBHelper.TABLE_STUDENTS + "(" + DBHelper.COLUMN_STUDENT_ID + ") ON DELETE CASCADE, " +
                "UNIQUE(" + DBHelper.COLUMN_COMPLETION_ASSIGNMENT_ID + ", " + DBHelper.COLUMN_COMPLETION_STUDENT_ID + "));");

        db.execSQL("CREATE TABLE " + DBHelper.TABLE_NOTIFICATIONS + " (" +
                DBHelper.COLUMN_NOTIFICATION_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_NOTIFICATION_USER_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_NOTIFICATION_TITLE + " TEXT NOT NULL, " +
                DBHelper.COLUMN_NOTIFICATION_MESSAGE + " TEXT NOT NULL, " +
                DBHelper.COLUMN_NOTIFICATION_TYPE + " TEXT NOT NULL CHECK(" + DBHelper.COLUMN_NOTIFICATION_TYPE + " IN ('NEW_GRADE', 'SCHEDULE_CHANGE', 'NEW_HOMEWORK', 'HOMEWORK_DEADLINE', 'ANNOUNCEMENT', 'ATTENDANCE', 'SYSTEM', 'MESSAGE')), " +
                DBHelper.COLUMN_IS_READ + " INTEGER DEFAULT 0 CHECK(" + DBHelper.COLUMN_IS_READ + " IN (0, 1)), " +
                DBHelper.COLUMN_NOTIFICATION_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                DBHelper.COLUMN_READ_AT + " DATETIME, " +
                DBHelper.COLUMN_ACTION_URL + " TEXT, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_NOTIFICATION_USER_ID + ") REFERENCES " + DBHelper.TABLE_USERS + "(" + DBHelper.COLUMN_USER_ID + ") ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE " + DBHelper.TABLE_ANNOUNCEMENTS + " (" +
                DBHelper.COLUMN_ANNOUNCEMENT_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_ANNOUNCEMENT_TITLE + " TEXT NOT NULL, " +
                DBHelper.COLUMN_ANNOUNCEMENT_CONTENT + " TEXT NOT NULL, " +
                DBHelper.COLUMN_AUTHOR_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_TARGET_GROUP_ID + " TEXT, " +
                DBHelper.COLUMN_PRIORITY + " TEXT DEFAULT 'NORMAL' CHECK(" + DBHelper.COLUMN_PRIORITY + " IN ('LOW', 'NORMAL', 'IMPORTANT', 'URGENT')), " +
                DBHelper.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                DBHelper.COLUMN_PUBLISHED_AT + " DATETIME, " +
                DBHelper.COLUMN_EXPIRES_AT + " DATETIME, " +
                DBHelper.COLUMN_IS_PUBLISHED + " INTEGER DEFAULT 0 CHECK(" + DBHelper.COLUMN_IS_PUBLISHED + " IN (0, 1)), " +
                DBHelper.COLUMN_IS_PINNED + " INTEGER DEFAULT 0 CHECK(" + DBHelper.COLUMN_IS_PINNED + " IN (0, 1)), " +
                DBHelper.COLUMN_ATTACHMENTS + " TEXT, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_AUTHOR_ID + ") REFERENCES " + DBHelper.TABLE_TEACHERS + "(" + DBHelper.COLUMN_TEACHER_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_TARGET_GROUP_ID + ") REFERENCES " + DBHelper.TABLE_GROUPS + "(" + DBHelper.COLUMN_GROUP_ID + ") ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE " + DBHelper.TABLE_ANNOUNCEMENT_READS + " (" +
                DBHelper.COLUMN_ANNOUNCEMENT_READ_ANN_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_ANNOUNCEMENT_READ_STUDENT_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_ANNOUNCEMENT_READ_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (" + DBHelper.COLUMN_ANNOUNCEMENT_READ_ANN_ID + ", " + DBHelper.COLUMN_ANNOUNCEMENT_READ_STUDENT_ID + "), " +
                "FOREIGN KEY (" + DBHelper.COLUMN_ANNOUNCEMENT_READ_ANN_ID + ") REFERENCES " + DBHelper.TABLE_ANNOUNCEMENTS + "(" + DBHelper.COLUMN_ANNOUNCEMENT_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_ANNOUNCEMENT_READ_STUDENT_ID + ") REFERENCES " + DBHelper.TABLE_STUDENTS + "(" + DBHelper.COLUMN_STUDENT_ID + ") ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE " + DBHelper.TABLE_BREAKS + " (" +
                DBHelper.COLUMN_BREAK_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_BREAK_GROUP_ID + " TEXT, " +
                DBHelper.COLUMN_BREAK_DAY_OF_WEEK + " INTEGER CHECK(" + DBHelper.COLUMN_BREAK_DAY_OF_WEEK + " BETWEEN 1 AND 7), " +
                DBHelper.COLUMN_BREAK_START_TIME + " TIME NOT NULL, " +
                DBHelper.COLUMN_BREAK_END_TIME + " TIME NOT NULL, " +
                DBHelper.COLUMN_BREAK_LABEL + " TEXT, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_BREAK_GROUP_ID + ") REFERENCES " + DBHelper.TABLE_GROUPS + "(" + DBHelper.COLUMN_GROUP_ID + ") ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE " + DBHelper.TABLE_USER_SETTINGS + " (" +
                DBHelper.COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_SETTING_NOTIFY_GRADES + " INTEGER DEFAULT 1 CHECK(" + DBHelper.COLUMN_SETTING_NOTIFY_GRADES + " IN (0, 1)), " +
                DBHelper.COLUMN_SETTING_NOTIFY_SCHEDULE + " INTEGER DEFAULT 1 CHECK(" + DBHelper.COLUMN_SETTING_NOTIFY_SCHEDULE + " IN (0, 1)), " +
                DBHelper.COLUMN_SETTING_NOTIFY_HOMEWORK + " INTEGER DEFAULT 1 CHECK(" + DBHelper.COLUMN_SETTING_NOTIFY_HOMEWORK + " IN (0, 1)), " +
                DBHelper.COLUMN_SETTING_NOTIFY_ANNOUNCE + " INTEGER DEFAULT 1 CHECK(" + DBHelper.COLUMN_SETTING_NOTIFY_ANNOUNCE + " IN (0, 1)), " +
                DBHelper.COLUMN_SETTING_THEME + " TEXT DEFAULT 'light' CHECK(" + DBHelper.COLUMN_SETTING_THEME + " IN ('light', 'dark', 'system')), " +
                DBHelper.COLUMN_SETTING_LANGUAGE + " TEXT DEFAULT 'ru', " +
                DBHelper.COLUMN_SETTING_LEARNING_SYSTEM + " TEXT DEFAULT 'trimester', " +
                "FOREIGN KEY (" + DBHelper.COLUMN_USER_ID + ") REFERENCES " + DBHelper.TABLE_USERS + "(" + DBHelper.COLUMN_USER_ID + ") ON DELETE CASCADE);");

        createSyncTables(db);
        createIndexes(db);
        createTriggers(db);
    }

    public static void upgradeSchema(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 14) {
            db.execSQL("ALTER TABLE " + DBHelper.TABLE_USER_SETTINGS + " ADD COLUMN " + DBHelper.COLUMN_SETTING_LEARNING_SYSTEM + " TEXT DEFAULT 'trimester'");
        }
        if (oldVersion < 15) {
            db.execSQL("DROP TABLE IF EXISTS " + DBHelper.TABLE_GRADES);
            db.execSQL("CREATE TABLE " + DBHelper.TABLE_GRADES + " (" +
                    DBHelper.COLUMN_GRADE_ID + " TEXT PRIMARY KEY, " +
                    DBHelper.COLUMN_GRADE_STUDENT_ID + " TEXT NOT NULL, " +
                    DBHelper.COLUMN_GRADE_SUBJECT_ID + " TEXT NOT NULL, " +
                    DBHelper.COLUMN_GRADE_TEACHER_ID + " TEXT NOT NULL, " +
                    DBHelper.COLUMN_GRADE_VALUE + " INTEGER NOT NULL DEFAULT 0, " +
                    DBHelper.COLUMN_GRADE_TYPE + " TEXT NOT NULL DEFAULT 'CURRENT', " +
                    DBHelper.COLUMN_GRADE_COMMENT + " TEXT, " +
                    DBHelper.COLUMN_GRADE_LESSON_ID + " TEXT, " +
                    DBHelper.COLUMN_GRADE_WEIGHT + " INTEGER DEFAULT 1, " +
                    DBHelper.COLUMN_GRADE_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    DBHelper.COLUMN_GRADE_UPDATED + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    DBHelper.COLUMN_GRADE_UPDATED_BY + " TEXT, " +
                    "FOREIGN KEY (" + DBHelper.COLUMN_GRADE_STUDENT_ID + ") REFERENCES " + DBHelper.TABLE_STUDENTS + "(" + DBHelper.COLUMN_STUDENT_ID + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY (" + DBHelper.COLUMN_GRADE_SUBJECT_ID + ") REFERENCES " + DBHelper.TABLE_SUBJECTS + "(" + DBHelper.COLUMN_SUBJECT_ID + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY (" + DBHelper.COLUMN_GRADE_TEACHER_ID + ") REFERENCES " + DBHelper.TABLE_TEACHERS + "(" + DBHelper.COLUMN_TEACHER_ID + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY (" + DBHelper.COLUMN_GRADE_UPDATED_BY + ") REFERENCES " + DBHelper.TABLE_TEACHERS + "(" + DBHelper.COLUMN_TEACHER_ID + ") ON DELETE SET NULL)");
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_grades_student ON " + DBHelper.TABLE_GRADES + "(" + DBHelper.COLUMN_GRADE_STUDENT_ID + ")");
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_grades_subject ON " + DBHelper.TABLE_GRADES + "(" + DBHelper.COLUMN_GRADE_SUBJECT_ID + ")");
        }
        if (oldVersion < 16) {
            db.execSQL("UPDATE " + DBHelper.TABLE_NOTIFICATIONS + " SET message = REPLACE(message, ' (CONTROL)', ' (Контрольная)') WHERE message LIKE '% (CONTROL)%'");
            db.execSQL("UPDATE " + DBHelper.TABLE_NOTIFICATIONS + " SET message = REPLACE(message, ' (CURRENT)', '') WHERE message LIKE '% (CURRENT)%'");
            db.execSQL("UPDATE " + DBHelper.TABLE_NOTIFICATIONS + " SET message = REPLACE(message, ' (TEST)', ' (Тест)') WHERE message LIKE '% (TEST)%'");
            db.execSQL("UPDATE " + DBHelper.TABLE_NOTIFICATIONS + " SET message = REPLACE(message, ' (PRACTICAL)', ' (Практическая)') WHERE message LIKE '% (PRACTICAL)%'");
            db.execSQL("UPDATE " + DBHelper.TABLE_NOTIFICATIONS + " SET message = REPLACE(message, ' (EXAM)', ' (Экзамен)') WHERE message LIKE '% (EXAM)%'");
            db.execSQL("UPDATE " + DBHelper.TABLE_NOTIFICATIONS + " SET message = REPLACE(message, ' (HOMEWORK)', ' (Домашнее задание)') WHERE message LIKE '% (HOMEWORK)%'");
            db.execSQL("UPDATE " + DBHelper.TABLE_NOTIFICATIONS + " SET message = REPLACE(message, ' (CREDIT_PASS)', ' (Зач)') WHERE message LIKE '% (CREDIT_PASS)%'");
            db.execSQL("UPDATE " + DBHelper.TABLE_NOTIFICATIONS + " SET message = REPLACE(message, ' (CREDIT)', ' (Зачёт)') WHERE message LIKE '% (CREDIT)%'");
        }
        if (oldVersion < 19) {
            migratePasswordsToBcrypt(db);
        }
        if (oldVersion < 20) {
            createSyncTables(db);
        }
        if (oldVersion < 21) {
            recreateGradesTableWithExtendedTypes(db);
        }
    }

    private static void createIndexes(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX idx_users_email ON " + DBHelper.TABLE_USERS + "(" + DBHelper.COLUMN_EMAIL + ")");
        db.execSQL("CREATE INDEX idx_users_role ON " + DBHelper.TABLE_USERS + "(" + DBHelper.COLUMN_ROLE + ")");
        db.execSQL("CREATE INDEX idx_students_group ON " + DBHelper.TABLE_STUDENTS + "(" + DBHelper.COLUMN_GROUP_ID + ")");
        db.execSQL("CREATE INDEX idx_grades_student ON " + DBHelper.TABLE_GRADES + "(" + DBHelper.COLUMN_GRADE_STUDENT_ID + ")");
        db.execSQL("CREATE INDEX idx_grades_subject ON " + DBHelper.TABLE_GRADES + "(" + DBHelper.COLUMN_GRADE_SUBJECT_ID + ")");
        db.execSQL("CREATE INDEX idx_attendance_student_date ON " + DBHelper.TABLE_ATTENDANCE + "(" + DBHelper.COLUMN_ATTENDANCE_STUDENT_ID + ", " + DBHelper.COLUMN_ATTENDANCE_DATE + ")");
        db.execSQL("CREATE INDEX idx_lessons_group_day ON " + DBHelper.TABLE_LESSONS + "(" + DBHelper.COLUMN_LESSON_GROUP_ID + ", " + DBHelper.COLUMN_DAY_OF_WEEK + ")");
        db.execSQL("CREATE INDEX idx_assignments_due_date ON " + DBHelper.TABLE_ASSIGNMENTS + "(" + DBHelper.COLUMN_DUE_DATE + ")");
        db.execSQL("CREATE INDEX idx_notifications_user ON " + DBHelper.TABLE_NOTIFICATIONS + "(" + DBHelper.COLUMN_NOTIFICATION_USER_ID + ", " + DBHelper.COLUMN_IS_READ + ")");
    }

    private static void createTriggers(SQLiteDatabase db) {
        db.execSQL("CREATE TRIGGER create_user_settings " +
                "AFTER INSERT ON " + DBHelper.TABLE_USERS + " " +
                "BEGIN " +
                "INSERT INTO " + DBHelper.TABLE_USER_SETTINGS + " (" + DBHelper.COLUMN_USER_ID + ") VALUES (NEW." + DBHelper.COLUMN_USER_ID + "); " +
                "END");
        db.execSQL("CREATE TRIGGER update_grade_timestamp " +
                "AFTER UPDATE ON " + DBHelper.TABLE_GRADES + " " +
                "BEGIN " +
                "UPDATE " + DBHelper.TABLE_GRADES + " SET " + DBHelper.COLUMN_GRADE_UPDATED + " = CURRENT_TIMESTAMP WHERE " + DBHelper.COLUMN_GRADE_ID + " = NEW." + DBHelper.COLUMN_GRADE_ID + "; " +
                "END");
    }

    private static void createSyncTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DBHelper.TABLE_SYNC_QUEUE + " (" +
                DBHelper.SQ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DBHelper.SQ_ENTITY + " TEXT NOT NULL, " +
                DBHelper.SQ_OP + " TEXT NOT NULL, " +
                DBHelper.SQ_LOCAL_ID + " TEXT, " +
                DBHelper.SQ_SERVER_ID + " TEXT, " +
                DBHelper.SQ_PAYLOAD + " TEXT, " +
                DBHelper.SQ_DEPENDS_ON + " TEXT, " +
                DBHelper.SQ_ATTEMPTS + " INTEGER NOT NULL DEFAULT 0, " +
                DBHelper.SQ_NEXT_ATTEMPT_AT + " INTEGER NOT NULL DEFAULT 0, " +
                DBHelper.SQ_CREATED_AT + " INTEGER NOT NULL DEFAULT 0);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DBHelper.TABLE_ID_MAP + " (" +
                DBHelper.IDM_ENTITY + " TEXT NOT NULL, " +
                DBHelper.IDM_LOCAL_ID + " TEXT NOT NULL, " +
                DBHelper.IDM_SERVER_ID + " TEXT NOT NULL, " +
                "PRIMARY KEY (" + DBHelper.IDM_ENTITY + ", " + DBHelper.IDM_LOCAL_ID + "));");
    }

    private static void recreateGradesTableWithExtendedTypes(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + DBHelper.TABLE_GRADES + " RENAME TO grades_migration_old");
        db.execSQL("CREATE TABLE " + DBHelper.TABLE_GRADES + " (" +
                DBHelper.COLUMN_GRADE_ID + " TEXT PRIMARY KEY, " +
                DBHelper.COLUMN_GRADE_STUDENT_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_GRADE_SUBJECT_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_GRADE_TEACHER_ID + " TEXT NOT NULL, " +
                DBHelper.COLUMN_GRADE_VALUE + " INTEGER NOT NULL DEFAULT 0, " +
                DBHelper.COLUMN_GRADE_TYPE + " TEXT NOT NULL DEFAULT 'CURRENT'" +
                " CHECK(" + DBHelper.COLUMN_GRADE_TYPE + " IN ('CURRENT','CONTROL','TEST','PRACTICAL','EXAM','CREDIT','HOMEWORK','ESSAY','TEST_PREP','LAB','ABSENT','EXEMPT','CREDIT_PASS')), " +
                DBHelper.COLUMN_GRADE_COMMENT + " TEXT, " +
                DBHelper.COLUMN_GRADE_LESSON_ID + " TEXT, " +
                DBHelper.COLUMN_GRADE_WEIGHT + " INTEGER DEFAULT 1, " +
                DBHelper.COLUMN_GRADE_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                DBHelper.COLUMN_GRADE_UPDATED + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                DBHelper.COLUMN_GRADE_UPDATED_BY + " TEXT, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_GRADE_STUDENT_ID + ") REFERENCES " + DBHelper.TABLE_STUDENTS + "(" + DBHelper.COLUMN_STUDENT_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_GRADE_SUBJECT_ID + ") REFERENCES " + DBHelper.TABLE_SUBJECTS + "(" + DBHelper.COLUMN_SUBJECT_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_GRADE_TEACHER_ID + ") REFERENCES " + DBHelper.TABLE_TEACHERS + "(" + DBHelper.COLUMN_TEACHER_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY (" + DBHelper.COLUMN_GRADE_UPDATED_BY + ") REFERENCES " + DBHelper.TABLE_TEACHERS + "(" + DBHelper.COLUMN_TEACHER_ID + ") ON DELETE SET NULL)");
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM grades_migration_old", null);
            while (c.moveToNext()) {
                ContentValues cv = new ContentValues();
                for (String col : c.getColumnNames()) {
                    int idx = c.getColumnIndex(col);
                    if (DBHelper.COLUMN_GRADE_TYPE.equals(col)) {
                        cv.put(col, DBHelper.normalizeGradeTypeForStorage(c.getString(idx)));
                    } else {
                        int type = c.getType(idx);
                        if (type == Cursor.FIELD_TYPE_NULL) continue;
                        if (type == Cursor.FIELD_TYPE_STRING) cv.put(col, c.getString(idx));
                        else if (type == Cursor.FIELD_TYPE_INTEGER) cv.put(col, c.getLong(idx));
                        else if (type == Cursor.FIELD_TYPE_FLOAT) cv.put(col, c.getDouble(idx));
                    }
                }
                db.insert(DBHelper.TABLE_GRADES, null, cv);
            }
        } finally {
            if (c != null) c.close();
        }
        db.execSQL("DROP TABLE grades_migration_old");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_grades_student ON " + DBHelper.TABLE_GRADES + "(" + DBHelper.COLUMN_GRADE_STUDENT_ID + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_grades_subject ON " + DBHelper.TABLE_GRADES + "(" + DBHelper.COLUMN_GRADE_SUBJECT_ID + ")");
    }

    private static void migratePasswordsToBcrypt(SQLiteDatabase db) {
        Cursor cursor = db.query(DBHelper.TABLE_USERS,
                new String[]{DBHelper.COLUMN_USER_ID, DBHelper.COLUMN_PASSWORD_HASH},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            String userId = cursor.getString(0);
            String storedPassword = cursor.getString(1);
            if (storedPassword == null || storedPassword.isEmpty() || PasswordHasher.isBcryptHash(storedPassword)) {
                continue;
            }
            ContentValues values = new ContentValues();
            values.put(DBHelper.COLUMN_PASSWORD_HASH, PasswordHasher.hash(storedPassword));
            db.update(DBHelper.TABLE_USERS, values, DBHelper.COLUMN_USER_ID + " = ?", new String[]{userId});
        }
        cursor.close();
    }
}
