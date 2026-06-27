package com.example.eduhub.network.sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.network.RetrofitClient;
import com.example.eduhub.network.util.ApiMapUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;


public final class SyncEngine {

    private static final String TAG = "SyncEngine";

    
    public static final String E_GRADE = "grade";
    public static final String E_ATTENDANCE = "attendance";
    public static final String E_ASSIGNMENT = "assignment";
    public static final String E_HOMEWORK_COMPLETION = "homework_completion";
    public static final String E_NOTIFICATION = "notification";
    public static final String E_USER = "user";
    public static final String E_USER_SETTINGS = "user_settings";
    public static final String E_ANNOUNCEMENT_READ = "announcement_read";
    public static final String E_LESSON = "lesson";

    public static final String OP_CREATE = "CREATE";
    public static final String OP_UPDATE = "UPDATE";
    public static final String OP_DELETE = "DELETE";

    private static final int MAX_ATTEMPTS = 12;
    private static final long DEFER_DELAY_MS = 3000L;

    private static final ExecutorService POOL = Executors.newSingleThreadExecutor();
    private static final Gson GSON = new Gson();

    
    private static final int SUCCESS = 0;
    private static final int FAIL = 1;
    private static final int DEFER = 2;

    private SyncEngine() {}


    public static String toJson(Map<String, Object> body) {
        return GSON.toJson(body == null ? new HashMap<>() : body);
    }

    public static void flush(Context ctx) {
        if (ctx == null) return;
        final Context app = ctx.getApplicationContext();
        POOL.execute(() -> {
            try {
                drain(app);
            } catch (Exception e) {
                Log.w(TAG, "flush failed", e);
            }
        });
    }


    private static void drain(Context app) {
        if (!isOnline(app)) return;
        DBHelper db = DBHelper.getInstance(app);
        try {
            boolean progress = true;
            int guard = 0;
            while (progress && guard++ < 30) {
                progress = false;
                List<Op> due = loadDue(db);
                if (due.isEmpty()) break;
                SyncUiNotifier.maybeBeginAutoSession(app, true);
                for (Op op : due) {
                    if (!isOnline(app)) return;
                    int result;
                    try {
                        result = process(app, db, op);
                    } catch (Exception e) {
                        Log.w(TAG, "process threw for " + op.entity + "/" + op.opType, e);
                        result = FAIL;
                    }
                    if (result == SUCCESS) {
                        SyncUiNotifier.recordUpload(op.entity);
                        deleteOp(db, op.id);
                        progress = true;
                    } else if (result == DEFER) {
                        scheduleRetry(db, op.id, op.attempts, true);
                    } else {
                        if (op.attempts + 1 >= MAX_ATTEMPTS) {
                            Log.w(TAG, "Dropping op after max attempts: " + op.entity + "/" + op.opType);
                            deleteOp(db, op.id);
                        } else {
                            scheduleRetry(db, op.id, op.attempts, false);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            SyncUiNotifier.endPushPhase();
        }
    }

    private static List<Op> loadDue(DBHelper db) {
        List<Op> list = new ArrayList<>();
        SQLiteDatabase sq = db.getReadableDatabase();
        Cursor c = null;
        try {
            c = sq.query(DBHelper.TABLE_SYNC_QUEUE, null,
                    DBHelper.SQ_NEXT_ATTEMPT_AT + " <= ?",
                    new String[]{String.valueOf(System.currentTimeMillis())},
                    null, null, DBHelper.SQ_CREATED_AT + " ASC, " + DBHelper.SQ_ID + " ASC");
            while (c.moveToNext()) {
                Op op = new Op();
                op.id = c.getLong(c.getColumnIndexOrThrow(DBHelper.SQ_ID));
                op.entity = c.getString(c.getColumnIndexOrThrow(DBHelper.SQ_ENTITY));
                op.opType = c.getString(c.getColumnIndexOrThrow(DBHelper.SQ_OP));
                op.localId = c.getString(c.getColumnIndexOrThrow(DBHelper.SQ_LOCAL_ID));
                op.serverId = c.getString(c.getColumnIndexOrThrow(DBHelper.SQ_SERVER_ID));
                op.payload = c.getString(c.getColumnIndexOrThrow(DBHelper.SQ_PAYLOAD));
                op.attempts = c.getInt(c.getColumnIndexOrThrow(DBHelper.SQ_ATTEMPTS));
                list.add(op);
            }
        } catch (Exception e) {
            Log.w(TAG, "loadDue failed", e);
        } finally {
            if (c != null) c.close();
        }
        return list;
    }

    private static void deleteOp(DBHelper db, long id) {
        try {
            db.getWritableDatabase().delete(DBHelper.TABLE_SYNC_QUEUE,
                    DBHelper.SQ_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (Exception ignored) {
        }
    }

    private static void scheduleRetry(DBHelper db, long id, int attempts, boolean defer) {
        long delay;
        int newAttempts;
        if (defer) {
            delay = DEFER_DELAY_MS;
            newAttempts = attempts; 
        } else {
            newAttempts = attempts + 1;
            delay = Math.min((long) Math.pow(2, newAttempts) * 1000L, 5 * 60 * 1000L);
        }
        try {
            android.content.ContentValues cv = new android.content.ContentValues();
            cv.put(DBHelper.SQ_ATTEMPTS, newAttempts);
            cv.put(DBHelper.SQ_NEXT_ATTEMPT_AT, System.currentTimeMillis() + delay);
            db.getWritableDatabase().update(DBHelper.TABLE_SYNC_QUEUE, cv,
                    DBHelper.SQ_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (Exception ignored) {
        }
    }


    @SuppressWarnings("unchecked")
    private static int process(Context app, DBHelper db, Op op) throws Exception {
        Map<String, Object> body = parseBody(op.payload);
        normalizeIds(db, body);
        normalizeGradePayload(body);

        switch (op.opType) {
            case OP_CREATE:
                return processCreate(app, db, op, body);
            case OP_UPDATE:
                return processUpdate(db, op, body);
            case OP_DELETE:
                return processDelete(db, op);
            default:
                return SUCCESS; 
        }
    }

    private static int processCreate(Context app, DBHelper db, Op op, Map<String, Object> body) throws Exception {
        switch (op.entity) {
            case E_GRADE: {
                Map<String, Object> created = exec(api().createGrade(body));
                if (created != null) {
                    return finishCreate(db, E_GRADE, op.localId, created);
                }
                
                return reconcileGrade(db, op.localId, body);
            }
            case E_ATTENDANCE: {
                Map<String, Object> created = exec(api().createAttendance(body));
                if (created != null) {
                    String serverId = idOf(created);
                    if (serverId != null) {
                        db.putIdMap(E_ATTENDANCE, op.localId, serverId);
                        db.reassignAttendanceId(op.localId, serverId);
                        return SUCCESS;
                    }
                }
                
                return reconcileAttendance(db, op.localId, body);
            }
            case E_ASSIGNMENT: {
                Map<String, Object> created = exec(api().createAssignment(body));
                if (created != null) {
                    String serverId = idOf(created);
                    if (serverId != null) {
                        db.putIdMap(E_ASSIGNMENT, op.localId, serverId);
                        db.reassignAssignmentId(op.localId, serverId);
                        return SUCCESS;
                    }
                }
                return FAIL;
            }
            case E_HOMEWORK_COMPLETION: {
                
                Object aid = body.get("assignmentId");
                if (aid != null && !isNumeric(String.valueOf(aid))) {
                    return DEFER;
                }
                Map<String, Object> created = exec(api().createHomeworkCompletion(body));
                if (created != null) {
                    String serverId = idOf(created);
                    if (serverId != null && op.localId != null) {
                        db.putIdMap(E_HOMEWORK_COMPLETION, op.localId, serverId);
                        db.reassignCompletionId(op.localId, serverId);
                    }
                    return SUCCESS;
                }
                return FAIL;
            }
            case E_NOTIFICATION: {
                exec(api().createNotification(body));
                return SUCCESS; 
            }
            case E_ANNOUNCEMENT_READ: {
                exec(api().createAnnouncementRead(body));
                return SUCCESS;
            }
            default:
                return SUCCESS;
        }
    }

    private static int processUpdate(DBHelper db, Op op, Map<String, Object> body) throws Exception {
        Integer id = resolveServerId(db, op.entity, op.serverId);
        if (id == null) {
            
            return isNumericOrMappable(op.serverId) ? DEFER : SUCCESS;
        }
        switch (op.entity) {
            case E_GRADE:
                exec(api().updateGrade(id, body));
                return SUCCESS;
            case E_ATTENDANCE:
                exec(api().updateAttendance(id, body));
                return SUCCESS;
            case E_ASSIGNMENT:
                exec(api().updateAssignment(id, body));
                return SUCCESS;
            case E_HOMEWORK_COMPLETION:
                exec(api().updateHomeworkCompletion(id, body));
                return SUCCESS;
            case E_NOTIFICATION:
                exec(api().updateNotification(id, body));
                return SUCCESS;
            case E_USER:
                exec(api().updateUser(id, body));
                return SUCCESS;
            case E_USER_SETTINGS:
                exec(api().updateUserSettings(id, body));
                return SUCCESS;
            case E_LESSON:
                exec(api().updateLesson(id, body));
                return SUCCESS;
            default:
                return SUCCESS;
        }
    }

    private static int processDelete(DBHelper db, Op op) throws Exception {
        Integer id = resolveServerId(db, op.entity, op.serverId);
        if (id == null) {
            
            return SUCCESS;
        }
        switch (op.entity) {
            case E_GRADE:
                exec(api().deleteGrade(id));
                return SUCCESS;
            case E_ATTENDANCE:
                exec(api().deleteAttendance(id));
                return SUCCESS;
            case E_ASSIGNMENT:
                exec(api().deleteAssignment(id));
                return SUCCESS;
            default:
                return SUCCESS;
        }
    }

    private static int finishCreate(DBHelper db, String entity, String localId, Map<String, Object> created) {
        if (created == null) return FAIL;
        String serverId = idOf(created);
        if (serverId == null) return FAIL;
        db.putIdMap(entity, localId, serverId);
        if (E_GRADE.equals(entity)) db.reassignGradeId(localId, serverId);
        return SUCCESS;
    }

    
    private static int reconcileGrade(DBHelper db, String localId, Map<String, Object> body) {
        Object sidObj = body.get("studentId");
        if (sidObj == null) return FAIL;
        Integer studentId;
        try {
            studentId = Integer.parseInt(ApiMapUtils.normalizeId(String.valueOf(sidObj)));
        } catch (NumberFormatException e) {
            return FAIL;
        }
        Integer lessonId = null;
        Object lidObj = body.get("lessonId");
        if (lidObj != null) {
            try {
                lessonId = Integer.parseInt(ApiMapUtils.normalizeId(String.valueOf(lidObj)));
            } catch (NumberFormatException ignored) {
            }
        }
        try {
            Map<String, String> filters = new HashMap<>();
            filters.put("studentId", String.valueOf(studentId));
            if (lessonId != null) {
                filters.put("lessonId", String.valueOf(lessonId));
            }
            List<Map<String, Object>> existing = execList(api().getGrades(filters));
            if (existing == null || existing.isEmpty()) return FAIL;
            Map<String, Object> row = existing.get(0);
            String serverId = ApiMapUtils.safeStr(row, "gradeId");
            if (serverId == null) serverId = ApiMapUtils.safeStr(row, "id");
            if (serverId == null || !isNumeric(serverId)) return FAIL;
            Map<String, Object> updated = exec(api().updateGrade(Integer.parseInt(serverId), body));
            if (updated != null) {
                db.putIdMap(E_GRADE, localId, serverId);
                db.reassignGradeId(localId, serverId);
                return SUCCESS;
            }
        } catch (Exception e) {
            Log.w(TAG, "grade reconcile failed", e);
        }
        return FAIL;
    }

    
    private static int reconcileAttendance(DBHelper db, String localId, Map<String, Object> body) {
        Object sidObj = body.get("studentId");
        Object dateObj = body.get("date");
        if (sidObj == null || dateObj == null) return FAIL;
        Integer studentId;
        try {
            studentId = Integer.parseInt(ApiMapUtils.normalizeId(String.valueOf(sidObj)));
        } catch (NumberFormatException e) {
            return FAIL;
        }
        String date = String.valueOf(dateObj);
        Integer lessonId = null;
        Object lidObj = body.get("lessonId");
        if (lidObj != null) {
            try {
                lessonId = Integer.parseInt(ApiMapUtils.normalizeId(String.valueOf(lidObj)));
            } catch (NumberFormatException ignored) {
            }
        }
        try {
            Map<String, String> filters = new HashMap<>();
            filters.put("studentId", String.valueOf(studentId));
            List<Map<String, Object>> existing = execList(api().getAttendance(filters));
            if (existing == null) return FAIL;
            for (Map<String, Object> row : existing) {
                String rowDate = ApiMapUtils.safeStr(row, "date");
                if (rowDate == null || !rowDate.equals(date)) continue;
                Integer rowLesson = ApiMapUtils.safeInt(row, "lessonId");
                if (lessonId != null && rowLesson != null && !lessonId.equals(rowLesson)) continue;
                if (lessonId != null && rowLesson == null) continue;
                String serverId = ApiMapUtils.safeStr(row, "id");
                if (serverId == null || !isNumeric(serverId)) continue;
                Map<String, Object> updated = exec(api().updateAttendance(Integer.parseInt(serverId), body));
                if (updated != null) {
                    db.putIdMap(E_ATTENDANCE, localId, serverId);
                    db.reassignAttendanceId(localId, serverId);
                    return SUCCESS;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "attendance reconcile failed", e);
        }
        return FAIL;
    }


    
    private static Integer resolveServerId(DBHelper db, String entity, String idValue) {
        if (idValue == null || idValue.isEmpty()) return null;
        if (isNumeric(idValue)) {
            return Integer.parseInt(ApiMapUtils.normalizeId(idValue));
        }
        String mapped = db.getMappedServerId(entity, idValue);
        if (mapped != null && isNumeric(mapped)) {
            return Integer.parseInt(ApiMapUtils.normalizeId(mapped));
        }
        return null;
    }

    private static boolean isNumericOrMappable(String idValue) {
        
        return idValue != null && !idValue.isEmpty();
    }

    
    private static void normalizeIds(DBHelper db, Map<String, Object> body) {
        if (body == null) return;
        String[] idKeys = {"studentId", "subjectId", "teacherId", "lessonId", "groupId",
                "userId", "authorId", "targetGroupId", "announcementId", "markedBy"};
        for (String key : idKeys) {
            if (!body.containsKey(key)) continue;
            Object v = body.get(key);
            if (v == null) continue;
            String s = String.valueOf(v);
            if (isNumeric(s)) {
                body.put(key, Long.parseLong(ApiMapUtils.normalizeId(s)));
            }
        }
        
        if (body.containsKey("assignmentId")) {
            Object v = body.get("assignmentId");
            if (v != null) {
                String s = String.valueOf(v);
                if (isNumeric(s)) {
                    body.put("assignmentId", Long.parseLong(ApiMapUtils.normalizeId(s)));
                } else {
                    String mapped = db.getMappedServerId(E_ASSIGNMENT, s);
                    if (mapped != null && isNumeric(mapped)) {
                        body.put("assignmentId", Long.parseLong(ApiMapUtils.normalizeId(mapped)));
                    }
                }
            }
        }
    }

    private static void normalizeGradePayload(Map<String, Object> body) {
        if (body == null) return;
        if (body.containsKey("gradeType")) {
            Object gt = body.get("gradeType");
            if (gt != null) {
                body.put("gradeType", DBHelper.normalizeGradeTypeForStorage(String.valueOf(gt)));
            }
        }
        if (body.containsKey("createdAt")) {
            Object ca = body.get("createdAt");
            if (ca != null) {
                String raw = String.valueOf(ca).trim();
                if (raw.length() >= 19 && raw.charAt(10) == ' ') {
                    body.put("createdAt", raw.substring(0, 10) + "T" + raw.substring(11, 19));
                } else if (raw.length() == 10) {
                    body.put("createdAt", raw + "T00:00:00");
                }
            }
        }
        Object lessonId = body.get("lessonId");
        if (lessonId == null) {
            body.remove("lessonId");
        }
    }

    private static boolean isNumeric(String id) {
        if (id == null || id.isEmpty()) return false;
        try {
            Integer.parseInt(ApiMapUtils.normalizeId(id));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static String idOf(Map<String, Object> body) {
        if (body == null) return null;
        String[] keys = {"id", "gradeId", "attendanceId", "assignmentId", "completionId", "notificationId"};
        for (String k : keys) {
            String id = ApiMapUtils.safeStr(body, k);
            if (id != null && !id.isEmpty()) return id;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseBody(String json) {
        if (json == null || json.isEmpty()) return new HashMap<>();
        try {
            Map<String, Object> raw = GSON.fromJson(json, Map.class);
            if (raw == null) return new HashMap<>();
            
            
            Map<String, Object> out = new HashMap<>();
            for (Map.Entry<String, Object> e : raw.entrySet()) {
                Object v = e.getValue();
                if (v instanceof Double) {
                    double d = (Double) v;
                    if (d == Math.floor(d) && !Double.isInfinite(d)) {
                        out.put(e.getKey(), (long) d);
                        continue;
                    }
                }
                out.put(e.getKey(), v);
            }
            return out;
        } catch (Exception e) {
            Log.w(TAG, "parseBody failed", e);
            return new HashMap<>();
        }
    }


    private static com.example.eduhub.network.ApiService api() {
        return RetrofitClient.getInstance().getApiService();
    }

    private static <T> T exec(Call<T> call) throws Exception {
        Response<T> response = call.execute();
        if (!response.isSuccessful()) {
            String err = null;
            try {
                if (response.errorBody() != null) err = response.errorBody().string();
            } catch (Exception ignored) {
            }
            Log.w(TAG, "HTTP " + response.code() + (err != null ? (": " + err) : ""));
            return null;
        }
        return response.body();
    }

    private static List<Map<String, Object>> execList(Call<List<Map<String, Object>>> call) throws Exception {
        Response<List<Map<String, Object>>> response = call.execute();
        if (!response.isSuccessful()) {
            Log.w(TAG, "fetch HTTP " + response.code());
            return null;
        }
        return response.body();
    }

    private static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }


    private static final class Op {
        long id;
        String entity;
        String opType;
        String localId;
        String serverId;
        String payload;
        int attempts;
    }
}
