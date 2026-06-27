package com.example.eduhub.marks;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.eduhub.appbars.ProfileNotificationFragment;
import com.example.eduhub.R;
import com.example.eduhub.marks.adapters.MarksDateAdapter;
import com.example.eduhub.marks.adapters.MarksSubjectAdapter;
import com.example.eduhub.marks.models.MarkDateItem;
import com.example.eduhub.marks.models.MarkSubjectItem;
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.ui.SyncRefreshFragment;

import java.util.List;

public class MarksFragment extends SyncRefreshFragment {

    private TextView tabByDate;
    private TextView tabBySubject;
    private TextView titleRecentGrades;
    private TextView tvEmpty;
    private RecyclerView recyclerMarks;

    private MarksDateAdapter dateAdapter;
    private MarksSubjectAdapter subjectAdapter;
    private boolean dateEmpty;
    private boolean subjectEmpty;
    private String studentId;
    private String userId;
    private boolean showingByDate = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            studentId = getArguments().getString("studentId", "");
            userId = getArguments().getString("userId", "");
        }
        if (studentId == null || studentId.isEmpty()) {
            studentId = UserSessionManager.getInstance(requireContext()).getStudentId();
        }
        return inflater.inflate(R.layout.fragment_marks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProfileNotificationFragment headerFragment = ProfileNotificationFragment.newInstance("Оценки", userId);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.header_fragment_container, headerFragment)
                .commit();

        tabByDate = view.findViewById(R.id.tab_by_date);
        tabBySubject = view.findViewById(R.id.tab_by_subject);
        titleRecentGrades = view.findViewById(R.id.title_recent_grades);
        tvEmpty = view.findViewById(R.id.tv_marks_empty);
        recyclerMarks = view.findViewById(R.id.recycler_marks);
        recyclerMarks.setLayoutManager(new LinearLayoutManager(requireContext()));

        setupAdapters();
        showTabByDate();

        tabByDate.setOnClickListener(v -> showTabByDate());
        tabBySubject.setOnClickListener(v -> showTabBySubject());

        SwipeRefreshLayout swipe = view.findViewById(R.id.swipe_refresh);
        if (swipe != null) {
            bindRefreshList(swipe, recyclerMarks);
        }
    }

    @Override
    protected void reloadAfterSync() {
        setupAdapters();
        if (showingByDate) {
            showTabByDate();
        } else {
            showTabBySubject();
        }
    }

    private void setupAdapters() {
        com.example.eduhub.marks.repository.GradesRepository repository =
            new com.example.eduhub.marks.repository.GradesRepository(requireContext(), studentId);

        List<MarkDateItem> dateItems = repository.getRecentGrades();
        dateAdapter = new MarksDateAdapter(dateItems);
        dateEmpty = dateItems.isEmpty();

        List<MarkSubjectItem> subjectItems = repository.getSubjectAverages();
        subjectAdapter = new MarksSubjectAdapter(subjectItems, studentId);
        subjectEmpty = subjectItems.isEmpty();
    }

    private void showTabByDate() {
        showingByDate = true;
        tabByDate.setBackgroundResource(R.drawable.bg_marks_toggle_selected);
        tabByDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_purple_main));

        tabBySubject.setBackgroundColor(Color.TRANSPARENT);
        tabBySubject.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_gray));

        titleRecentGrades.setVisibility(View.VISIBLE);
        recyclerMarks.setAdapter(dateAdapter);
        updateEmptyState(dateEmpty, "Оценок пока нет");
    }

    private void updateEmptyState(boolean empty, String message) {
        if (tvEmpty != null) {
            tvEmpty.setText(message);
            tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        }
        recyclerMarks.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void showTabBySubject() {
        showingByDate = false;
        tabBySubject.setBackgroundResource(R.drawable.bg_marks_toggle_selected);
        tabBySubject.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_purple_main));

        tabByDate.setBackgroundColor(Color.TRANSPARENT);
        tabByDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_gray));

        titleRecentGrades.setVisibility(View.GONE);
        recyclerMarks.setAdapter(subjectAdapter);
        updateEmptyState(subjectEmpty, "Предметов пока нет");
    }
}
