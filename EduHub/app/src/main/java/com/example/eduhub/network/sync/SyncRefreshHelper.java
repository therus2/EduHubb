package com.example.eduhub.network.sync;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.eduhub.R;
import com.example.eduhub.network.session.UserSessionManager;

public final class SyncRefreshHelper {

    private static final long TIMEOUT_MS = 30_000L;
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private SyncRefreshHelper() {}

    public static void triggerSync(@NonNull Fragment fragment,
                                   @Nullable SwipeRefreshLayout swipeRefresh) {
        Context ctx = fragment.getContext();
        if (ctx == null) return;
        Context app = ctx.getApplicationContext();
        String uid = resolveUserId(fragment);
        String role = UserSessionManager.getInstance(app).getRole();
        if (uid == null || uid.isEmpty()) {
            stopRefreshing(swipeRefresh);
            return;
        }
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(true);
            MAIN.postDelayed(() -> stopRefreshing(swipeRefresh), TIMEOUT_MS);
        }
        SyncUiNotifier.beginSession(app, SyncUiNotifier.Mode.MANUAL);
        SyncEngine.flush(app);
        new UserDataSyncManager(app).refreshInBackground(uid, role);
    }

    public static void bindList(@NonNull Fragment fragment,
                                @NonNull SwipeRefreshLayout swipeRefresh,
                                @NonNull RecyclerView recyclerView) {
        styleSwipe(swipeRefresh);
        Runnable updateEnabled = () ->
                swipeRefresh.setEnabled(!recyclerView.canScrollVertically(-1));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                updateEnabled.run();
            }
        });
        recyclerView.post(updateEnabled);
        swipeRefresh.setOnRefreshListener(() -> triggerSync(fragment, swipeRefresh));
    }

    public static void bindScroll(@NonNull Fragment fragment,
                                  @NonNull SwipeRefreshLayout swipeRefresh,
                                  @NonNull View scrollable) {
        styleSwipe(swipeRefresh);
        Runnable updateEnabled = () ->
                swipeRefresh.setEnabled(!scrollable.canScrollVertically(-1));
        scrollable.setOnScrollChangeListener((v, x, y, ox, oy) -> updateEnabled.run());
        scrollable.post(updateEnabled);
        swipeRefresh.setOnRefreshListener(() -> triggerSync(fragment, swipeRefresh));
    }

    public static void stopRefreshing(@Nullable SwipeRefreshLayout swipeRefresh) {
        if (swipeRefresh != null) {
            MAIN.post(() -> swipeRefresh.setRefreshing(false));
        }
    }

    @Nullable
    private static String resolveUserId(Fragment fragment) {
        UserSessionManager session = UserSessionManager.getInstance(
                fragment.requireContext().getApplicationContext());
        String uid = session.getUserId();
        if (fragment.getArguments() != null) {
            String argUser = fragment.getArguments().getString("userId");
            if (argUser != null && !argUser.isEmpty()) return argUser;
            String argTeacher = fragment.getArguments().getString("teacherId");
            if (argTeacher != null && !argTeacher.isEmpty()) return argTeacher;
        }
        return uid;
    }

    private static void styleSwipe(SwipeRefreshLayout swipeRefresh) {
        swipeRefresh.setColorSchemeResources(R.color.marks_purple_main);
    }
}
