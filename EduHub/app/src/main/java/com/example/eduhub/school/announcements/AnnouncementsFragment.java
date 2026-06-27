package com.example.eduhub.school.announcements;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.example.eduhub.school.announcements.models.AnnouncementItem;
import com.example.eduhub.school.announcements.repository.AnnouncementsRepository;

import java.util.List;

public class AnnouncementsFragment extends Fragment {

    private String studentId;
    private AnnouncementsRepository repository;
    private RecyclerView recycler;
    private AnnouncementsAdapter adapter;
    private TextView tabUnread, tabArchive;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            studentId = getArguments().getString("studentId", "");
        }
        return inflater.inflate(R.layout.fragment_announcements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            com.example.eduhub.appbars.BackHeaderFragment headerFragment =
                    com.example.eduhub.appbars.BackHeaderFragment.newInstance("Объявления");
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.header_fragment_container, headerFragment)
                    .commit();
        } catch (Exception e) {
            Log.e("AnnouncementsFrag", "header error", e);
        }

        String groupId = resolveGroupId(studentId);
        repository = new AnnouncementsRepository(requireContext(), groupId, studentId);

        tabUnread = view.findViewById(R.id.tab_unread);
        tabArchive = view.findViewById(R.id.tab_archive);
        recycler = view.findViewById(R.id.recycler_announcements);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        tabUnread.setOnClickListener(v -> showUnread());
        tabArchive.setOnClickListener(v -> showArchive());

        showUnread();
    }

    private void showUnread() {
        tabUnread.setBackgroundResource(R.drawable.bg_marks_toggle_selected);
        tabUnread.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_purple_main));
        tabArchive.setBackgroundColor(Color.TRANSPARENT);
        tabArchive.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_gray));

        List<AnnouncementItem> items = repository.getUnreadAnnouncements();
        adapter = new AnnouncementsAdapter(items, (announcementId, position) -> {
            if (announcementId == null || announcementId.isEmpty()) return;
            repository.markAsRead(announcementId);
            showUnread();
        });
        recycler.setAdapter(adapter);
    }

    private void showArchive() {
        tabArchive.setBackgroundResource(R.drawable.bg_marks_toggle_selected);
        tabArchive.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_purple_main));
        tabUnread.setBackgroundColor(Color.TRANSPARENT);
        tabUnread.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_gray));

        List<AnnouncementItem> items = repository.getArchivedAnnouncements();
        adapter = new AnnouncementsAdapter(items, null);
        recycler.setAdapter(adapter);
    }

    private String resolveGroupId(String studentId) {
        if (studentId == null || studentId.isEmpty()) return "";
        com.example.eduhub.database.DBHelper dbHelper = new com.example.eduhub.database.DBHelper(requireContext());
        android.database.Cursor c = dbHelper.getReadableDatabase().rawQuery(
                "SELECT group_id FROM students WHERE id = ?", new String[]{studentId});
        String gid = "";
        if (c.moveToFirst()) {
            gid = c.getString(0);
        }
        c.close();
        dbHelper.close();
        return gid;
    }
}