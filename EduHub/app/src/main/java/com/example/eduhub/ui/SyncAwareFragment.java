package com.example.eduhub.ui;

import androidx.fragment.app.Fragment;

import com.example.eduhub.network.sync.DataSyncEvents;

public abstract class SyncAwareFragment extends Fragment implements DataSyncEvents.Listener {

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
        reloadAfterSync();
    }
}
