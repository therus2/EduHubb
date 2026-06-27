package com.example.eduhub.ui;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.eduhub.network.sync.DataSyncEvents;
import com.example.eduhub.network.sync.SyncRefreshHelper;

public abstract class SyncRefreshFragment extends Fragment implements DataSyncEvents.Listener {

    protected SwipeRefreshLayout swipeRefresh;

    protected void bindRefreshList(@NonNull SwipeRefreshLayout swipe, @NonNull RecyclerView list) {
        swipeRefresh = swipe;
        SyncRefreshHelper.bindList(this, swipe, list);
    }

    protected void bindRefreshScroll(@NonNull SwipeRefreshLayout swipe, @NonNull View scroll) {
        swipeRefresh = swipe;
        SyncRefreshHelper.bindScroll(this, swipe, scroll);
    }

    protected abstract void reloadAfterSync();

    @Override
    public void onResume() {
        super.onResume();
        DataSyncEvents.register(this);
    }

    @Override
    public void onPause() {
        DataSyncEvents.unregister(this);
        super.onPause();
    }

    @Override
    public void onBackgroundSyncComplete() {
        if (!isAdded()) return;
        SyncRefreshHelper.stopRefreshing(swipeRefresh);
        reloadAfterSync();
    }
}
