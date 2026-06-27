package com.example.eduhub.notifications;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.notifications.models.NotificationItem;
import com.example.eduhub.notifications.repository.NotificationsRepository;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private String userId;
    private NotificationsRepository repository;
    private RecyclerView recycler;
    private NotificationsAdapter adapter;
    private TextView tabUnread, tabArchive;
    private boolean showingArchive = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            userId = getArguments().getString("userId", "");
        }
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        com.example.eduhub.appbars.BackHeaderFragment headerFragment =
                com.example.eduhub.appbars.BackHeaderFragment.newInstance("Уведомления");
        getChildFragmentManager().beginTransaction()
                .replace(R.id.header_fragment_container, headerFragment)
                .commit();

        repository = new NotificationsRepository(requireContext(), userId);

        tabUnread = view.findViewById(R.id.tab_unread);
        tabArchive = view.findViewById(R.id.tab_archive);
        recycler = view.findViewById(R.id.recycler_notifications);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        tabUnread.setOnClickListener(v -> showUnread());
        tabArchive.setOnClickListener(v -> showArchive());

        showUnread();
    }

    private void showUnread() {
        showingArchive = false;
        tabUnread.setBackgroundResource(R.drawable.bg_marks_toggle_selected);
        tabUnread.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_purple_main));
        tabArchive.setBackgroundColor(Color.TRANSPARENT);
        tabArchive.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_gray));

        List<NotificationItem> items = repository.getUnreadNotifications();
        adapter = new NotificationsAdapter(items, (notificationId, position) -> {
            if (notificationId == null || notificationId.isEmpty()) return;
            repository.markAsRead(notificationId);
            showUnread();
        });
        recycler.setAdapter(adapter);
    }

    private void showArchive() {
        showingArchive = true;
        tabArchive.setBackgroundResource(R.drawable.bg_marks_toggle_selected);
        tabArchive.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_purple_main));
        tabUnread.setBackgroundColor(Color.TRANSPARENT);
        tabUnread.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_gray));

        List<NotificationItem> items = repository.getArchivedNotifications();
        adapter = new NotificationsAdapter(items, null);
        recycler.setAdapter(adapter);
    }
}