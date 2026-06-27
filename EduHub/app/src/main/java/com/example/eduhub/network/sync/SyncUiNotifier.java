package com.example.eduhub.network.sync;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public final class SyncUiNotifier {

    public static final int LARGE_SYNC_THRESHOLD = 30;
    private static final long DEBOUNCE_MS = 600L;

    public enum Mode {
        MANUAL,
        BACKGROUND,
        AUTO
    }

    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private static final Object LOCK = new Object();

    private static volatile Context appContext;
    private static volatile Mode sessionMode;
    private static volatile boolean sessionActive;
    private static volatile boolean pushPhaseDone;
    private static volatile boolean pullPhaseDone;

    private static final Map<String, AtomicInteger> UPLOAD = new ConcurrentHashMap<>();
    private static final Map<String, AtomicInteger> DOWNLOAD = new ConcurrentHashMap<>();

    private static final Runnable SHOW_RUNNABLE = SyncUiNotifier::showToastIfReady;

    private SyncUiNotifier() {}

    public static void beginSession(Context context, Mode mode) {
        if (context == null || mode == null) return;
        synchronized (LOCK) {
            appContext = context.getApplicationContext();
            if (!sessionActive) {
                UPLOAD.clear();
                DOWNLOAD.clear();
                pushPhaseDone = false;
                pullPhaseDone = false;
                sessionMode = mode;
                sessionActive = true;
            } else if (mode == Mode.MANUAL) {
                sessionMode = Mode.MANUAL;
            }
        }
    }

    public static void endPushPhase() {
        synchronized (LOCK) {
            if (!sessionActive) return;
            pushPhaseDone = true;
        }
        scheduleShowIfReady();
    }

    public static void endPullPhase() {
        synchronized (LOCK) {
            if (!sessionActive) return;
            pullPhaseDone = true;
        }
        scheduleShowIfReady();
    }

    public static void endSession() {
        synchronized (LOCK) {
            if (!sessionActive) return;
            pullPhaseDone = true;
        }
        scheduleShowIfReady();
    }

    public static void recordUpload(String entity) {
        if (entity == null || entity.isEmpty()) return;
        increment(UPLOAD, entity, 1);
    }

    public static void recordDownload(String key, int count) {
        if (key == null || key.isEmpty() || count <= 0) return;
        increment(DOWNLOAD, key, count);
    }

    public static void maybeBeginAutoSession(Context context, boolean queueHasWork) {
        if (context == null || !queueHasWork) return;
        synchronized (LOCK) {
            if (!sessionActive) {
                beginSession(context, Mode.AUTO);
            }
        }
    }

    private static void increment(Map<String, AtomicInteger> map, String key, int delta) {
        map.computeIfAbsent(key, k -> new AtomicInteger(0)).addAndGet(delta);
    }

    private static void scheduleShowIfReady() {
        if (!isReadyToShow()) return;
        MAIN.removeCallbacks(SHOW_RUNNABLE);
        MAIN.postDelayed(SHOW_RUNNABLE, DEBOUNCE_MS);
    }

    private static boolean isReadyToShow() {
        synchronized (LOCK) {
            if (!sessionActive) return false;
            Mode mode = sessionMode != null ? sessionMode : Mode.BACKGROUND;
            if (mode == Mode.MANUAL || mode == Mode.BACKGROUND) {
                return pullPhaseDone;
            }
            int uploadTotal = 0;
            for (AtomicInteger v : UPLOAD.values()) uploadTotal += v.get();
            return pushPhaseDone && (pullPhaseDone || uploadTotal > 0);
        }
    }

    private static void showToastIfReady() {
        Context ctx;
        Mode mode;
        Map<String, Integer> up;
        Map<String, Integer> down;
        synchronized (LOCK) {
            if (!sessionActive || appContext == null) return;
            ctx = appContext;
            mode = sessionMode != null ? sessionMode : Mode.BACKGROUND;
            up = snapshot(UPLOAD);
            down = snapshot(DOWNLOAD);
            sessionActive = false;
            pushPhaseDone = false;
            pullPhaseDone = false;
            UPLOAD.clear();
            DOWNLOAD.clear();
        }
        int uploadTotal = sum(up);
        int downloadTotal = sum(down);
        int total = uploadTotal + downloadTotal;

        if (mode == Mode.AUTO && uploadTotal == 0 && downloadTotal < LARGE_SYNC_THRESHOLD) {
            return;
        }
        if (total == 0 && mode != Mode.MANUAL) {
            return;
        }

        String message = formatMessage(up, down, uploadTotal, downloadTotal, total, mode);
        int duration = (mode != Mode.MANUAL && total >= LARGE_SYNC_THRESHOLD)
                ? Toast.LENGTH_SHORT
                : Toast.LENGTH_LONG;
        MAIN.post(() -> Toast.makeText(ctx, message, duration).show());
    }

    private static Map<String, Integer> snapshot(Map<String, AtomicInteger> src) {
        Map<String, Integer> out = new LinkedHashMap<>();
        for (Map.Entry<String, AtomicInteger> e : src.entrySet()) {
            int v = e.getValue().get();
            if (v > 0) out.put(e.getKey(), v);
        }
        return out;
    }

    private static int sum(Map<String, Integer> m) {
        int s = 0;
        for (int v : m.values()) s += v;
        return s;
    }

    private static String formatMessage(Map<String, Integer> up, Map<String, Integer> down,
                                        int uploadTotal, int downloadTotal, int total, Mode mode) {
        if (mode != Mode.MANUAL && total >= LARGE_SYNC_THRESHOLD) {
            return String.format(Locale.getDefault(),
                    "Синхронизация: ↑%d ↓%d", uploadTotal, downloadTotal);
        }
        StringBuilder sb = new StringBuilder("Синхронизация");
        if (total == 0 && mode == Mode.MANUAL) {
            sb.append("\nНет изменений");
            return sb.toString();
        }
        String upLine = formatLine("↑ На сервер: ", up);
        String downLine = formatLine("↓ С сервера: ", down);
        if (upLine != null) sb.append("\n").append(upLine);
        if (downLine != null) sb.append("\n").append(downLine);
        return sb.toString();
    }

    private static String formatLine(String prefix, Map<String, Integer> items) {
        if (items.isEmpty()) return null;
        StringBuilder sb = new StringBuilder(prefix);
        boolean first = true;
        for (Map.Entry<String, Integer> e : items.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(labelFor(e.getKey())).append(" ").append(e.getValue());
            first = false;
        }
        return sb.toString();
    }

    private static String labelFor(String key) {
        if (key == null) return "данные";
        switch (key) {
            case SyncEngine.E_GRADE:
            case "grades":
                return "оценки";
            case SyncEngine.E_ATTENDANCE:
                return "посещаемость";
            case SyncEngine.E_ASSIGNMENT:
            case "assignments":
                return "задания";
            case SyncEngine.E_HOMEWORK_COMPLETION:
            case "homework":
                return "домашние работы";
            case SyncEngine.E_NOTIFICATION:
            case "notifications":
                return "уведомления";
            case SyncEngine.E_USER:
                return "профиль";
            case SyncEngine.E_USER_SETTINGS:
                return "настройки";
            case SyncEngine.E_ANNOUNCEMENT_READ:
                return "объявления";
            case SyncEngine.E_LESSON:
            case "lessons":
                return "уроки";
            case "students":
                return "студенты";
            case "subjects":
                return "предметы";
            case "teachers":
                return "преподаватели";
            case "groups":
                return "группы";
            case "breaks":
                return "перемены";
            default:
                return key;
        }
    }
}
