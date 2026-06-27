package com.example.eduhub.network.sync;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.CopyOnWriteArrayList;


public final class DataSyncEvents {

    private static final String TAG = "DataSyncEvents";

    public interface Listener {
        void onBackgroundSyncComplete();
    }

    private static final CopyOnWriteArrayList<Listener> LISTENERS = new CopyOnWriteArrayList<>();
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private DataSyncEvents() {}

    public static void register(Listener listener) {
        if (listener != null && !LISTENERS.contains(listener)) {
            LISTENERS.add(listener);
        }
    }

    public static void unregister(Listener listener) {
        LISTENERS.remove(listener);
    }

    public static void notifyBackgroundComplete() {
        MAIN.post(() -> {
            for (Listener listener : LISTENERS) {
                try {
                    listener.onBackgroundSyncComplete();
                } catch (Exception e) {
                    Log.w(TAG, "listener failed", e);
                }
            }
        });
    }
}
