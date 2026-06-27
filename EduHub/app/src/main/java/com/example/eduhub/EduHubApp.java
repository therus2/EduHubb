package com.example.eduhub;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.eduhub.network.sync.SyncEngine;
import com.example.eduhub.network.sync.SyncWorker;

import java.util.concurrent.TimeUnit;

import dagger.hilt.android.HiltAndroidApp;


@HiltAndroidApp
public class EduHubApp extends Application {

    private static EduHubApp instance;

    public static EduHubApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        
        SyncEngine.flush(this);

        registerConnectivityFlush();
        schedulePeriodicSync();
    }

    
    private void registerConnectivityFlush() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (cm == null) return;
            NetworkRequest request = new NetworkRequest.Builder().build();
            cm.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    SyncEngine.flush(EduHubApp.this);
                }
            });
        } catch (Exception ignored) {
        }
    }

    
    private void schedulePeriodicSync() {
        try {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                    SyncWorker.class, 15, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build();
            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    "eduhub_outbox_sync",
                    ExistingPeriodicWorkPolicy.KEEP,
                    request);
        } catch (Exception ignored) {
        }
    }
}
