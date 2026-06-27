package com.example.eduhub.network.sync;

import android.content.Context;
import android.util.Log;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.network.util.ApiMapUtils;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public final class ServerSyncManager {

    private static final String TAG = "ServerSync";
    private static final ExecutorService POOL = Executors.newSingleThreadExecutor();

    private ServerSyncManager() {}


    public static void assignmentCreate(Context ctx, String localId, Map<String, Object> body) {
        enqueue(ctx, SyncEngine.E_ASSIGNMENT, SyncEngine.OP_CREATE, localId, null, body);
    }

    public static void assignmentUpdate(Context ctx, String assignmentId, Map<String, Object> body) {
        enqueue(ctx, SyncEngine.E_ASSIGNMENT, SyncEngine.OP_UPDATE, null, assignmentId, body);
    }

    public static void assignmentDelete(Context ctx, String assignmentId) {
        enqueue(ctx, SyncEngine.E_ASSIGNMENT, SyncEngine.OP_DELETE, null, assignmentId, null);
    }


    public static void gradeCreate(Context ctx, String localId, Map<String, Object> body) {
        enqueue(ctx, SyncEngine.E_GRADE, SyncEngine.OP_CREATE, localId, null, body);
    }

    public static void gradeUpdate(Context ctx, String gradeId, Map<String, Object> body) {
        enqueue(ctx, SyncEngine.E_GRADE, SyncEngine.OP_UPDATE, null, gradeId, body);
    }

    public static void gradeDelete(Context ctx, String gradeId) {
        enqueue(ctx, SyncEngine.E_GRADE, SyncEngine.OP_DELETE, null, gradeId, null);
    }


    public static void attendanceCreate(Context ctx, String localId, Map<String, Object> body) {
        enqueue(ctx, SyncEngine.E_ATTENDANCE, SyncEngine.OP_CREATE, localId, null, body);
    }

    public static void attendanceUpdate(Context ctx, String attendanceId, Map<String, Object> body) {
        enqueue(ctx, SyncEngine.E_ATTENDANCE, SyncEngine.OP_UPDATE, null, attendanceId, body);
    }

    public static void attendanceDelete(Context ctx, String attendanceId) {
        enqueue(ctx, SyncEngine.E_ATTENDANCE, SyncEngine.OP_DELETE, null, attendanceId, null);
    }


    public static void homeworkCompletionUpdate(Context ctx, String completionId, Map<String, Object> body) {
        if (isRemoteId(completionId)) {
            enqueue(ctx, SyncEngine.E_HOMEWORK_COMPLETION, SyncEngine.OP_UPDATE, null, completionId, body);
        } else {
            
            enqueue(ctx, SyncEngine.E_HOMEWORK_COMPLETION, SyncEngine.OP_CREATE, completionId, null, body);
        }
    }


    public static void notificationCreate(Context ctx, Map<String, Object> body) {
        enqueue(ctx, SyncEngine.E_NOTIFICATION, SyncEngine.OP_CREATE, null, null, body);
    }

    public static void notificationUpdate(Context ctx, String notificationId, Map<String, Object> body) {
        enqueue(ctx, SyncEngine.E_NOTIFICATION, SyncEngine.OP_UPDATE, null, notificationId, body);
    }


    public static void userUpdate(Context ctx, String userId, Map<String, Object> body) {
        enqueue(ctx, SyncEngine.E_USER, SyncEngine.OP_UPDATE, null, userId, body);
    }

    public static void userSettingsUpdate(Context ctx, String userId, Map<String, Object> body) {
        enqueue(ctx, SyncEngine.E_USER_SETTINGS, SyncEngine.OP_UPDATE, null, userId, body);
    }

    public static void announcementReadCreate(Context ctx, Map<String, Object> body) {
        enqueue(ctx, SyncEngine.E_ANNOUNCEMENT_READ, SyncEngine.OP_CREATE, null, null, body);
    }

    public static void lessonUpdate(Context ctx, String lessonId, Map<String, Object> body) {
        enqueue(ctx, SyncEngine.E_LESSON, SyncEngine.OP_UPDATE, null, lessonId, body);
    }


    private static void enqueue(Context ctx, String entity, String op, String localId,
                                String serverId, Map<String, Object> body) {
        if (ctx == null) return;
        final Context app = ctx.getApplicationContext();
        final String payload = body != null ? SyncEngine.toJson(body) : null;
        POOL.execute(() -> {
            DBHelper db = DBHelper.getInstance(app);
            try {
                db.enqueueSyncOp(entity, op, localId, serverId, payload, null);
            } catch (Exception e) {
                Log.w(TAG, "enqueue failed for " + entity + "/" + op, e);
            }
            SyncEngine.flush(app);
        });
    }

    
    private static boolean isRemoteId(String id) {
        if (id == null || id.isEmpty()) return false;
        try {
            Integer.parseInt(ApiMapUtils.normalizeId(id));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
