package com.example.eduhub.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.eduhub.appbars.ProfileNotificationFragment;
import com.example.eduhub.R;
import com.example.eduhub.tasks.adapters.TasksAdapter;
import com.example.eduhub.tasks.repository.TasksRepository;
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.ui.SyncRefreshFragment;
import com.example.eduhub.network.sync.SyncEngine;

public class TasksFragment extends SyncRefreshFragment {

    private TextView tabActive;
    private TextView tabCompleted;
    private TextView tabOverdue;
    private TextView tvEmpty;
    private RecyclerView recyclerTasks;

    private TasksAdapter activeAdapter;
    private TasksAdapter completedAdapter;
    private TasksAdapter overdueAdapter;
    private TasksRepository repo;
    private String studentId;
    private String userId;
    private int activeTab = 0;

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
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabActive = view.findViewById(R.id.tab_active);
        tabCompleted = view.findViewById(R.id.tab_completed);
        tabOverdue = view.findViewById(R.id.tab_overdue);
        tvEmpty = view.findViewById(R.id.tv_tasks_empty);
        recyclerTasks = view.findViewById(R.id.recycler_tasks);

        ProfileNotificationFragment headerFragment = ProfileNotificationFragment.newInstance("Задания", userId);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.header_fragment_container, headerFragment)
                .commit();

        recyclerTasks.setLayoutManager(new LinearLayoutManager(requireContext()));

        repo = new TasksRepository(requireContext(), studentId);
        setupAdapters();
        showTabActive();

        tabActive.setOnClickListener(v -> showTabActive());
        tabCompleted.setOnClickListener(v -> showTabCompleted());
        tabOverdue.setOnClickListener(v -> showTabOverdue());

        SwipeRefreshLayout swipe = view.findViewById(R.id.swipe_refresh);
        if (swipe != null) {
            bindRefreshList(swipe, recyclerTasks);
        }
    }

    @Override
    protected void reloadAfterSync() {
        setupAdapters();
        switch (activeTab) {
            case 1: showTabCompleted(); break;
            case 2: showTabOverdue(); break;
            default: showTabActive(); break;
        }
    }

    private void setupAdapters() {
        activeAdapter = new TasksAdapter(repo.getActiveTasks());
        completedAdapter = new TasksAdapter(repo.getCompletedTasks());
        overdueAdapter = new TasksAdapter(repo.getOverdueTasks());

        TasksAdapter.OnTaskActionListener listener = (assignmentId, comment) -> {
            boolean ok = repo.submitHomework(assignmentId, comment);
            if (ok) {
                SyncEngine.flush(requireContext().getApplicationContext());
                Toast.makeText(requireContext(), "Работа отправлена на проверку!", Toast.LENGTH_SHORT).show();
                refreshData();
            } else {
                Toast.makeText(requireContext(), "Ошибка при отправке", Toast.LENGTH_SHORT).show();
            }
        };

        activeAdapter.setOnTaskActionListener(listener);
        completedAdapter.setOnTaskActionListener(listener);
        overdueAdapter.setOnTaskActionListener(listener);
    }

    private void refreshData() {
        activeAdapter = new TasksAdapter(repo.getActiveTasks());
        completedAdapter = new TasksAdapter(repo.getCompletedTasks());
        overdueAdapter = new TasksAdapter(repo.getOverdueTasks());

        TasksAdapter.OnTaskActionListener listener = (assignmentId, comment) -> {
            boolean ok = repo.submitHomework(assignmentId, comment);
            if (ok) {
                SyncEngine.flush(requireContext().getApplicationContext());
                Toast.makeText(requireContext(), "Работа отправлена на проверку!", Toast.LENGTH_SHORT).show();
                refreshData();
            } else {
                Toast.makeText(requireContext(), "Ошибка при отправке", Toast.LENGTH_SHORT).show();
            }
        };
        activeAdapter.setOnTaskActionListener(listener);
        completedAdapter.setOnTaskActionListener(listener);
        overdueAdapter.setOnTaskActionListener(listener);

        recyclerTasks.setAdapter(activeAdapter);
    }

    private void resetTabs() {
        tabActive.setBackgroundResource(R.drawable.bg_tasks_tab_unselected);
        tabActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_gray));

        tabCompleted.setBackgroundResource(R.drawable.bg_tasks_tab_unselected);
        tabCompleted.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_gray));

        tabOverdue.setBackgroundResource(R.drawable.bg_tasks_tab_unselected);
        tabOverdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_gray));
    }

    private void showTabActive() {
        activeTab = 0;
        resetTabs();
        tabActive.setBackgroundResource(R.drawable.bg_tasks_tab_selected);
        tabActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        recyclerTasks.setAdapter(activeAdapter);
        updateEmptyState(activeAdapter, "Нет активных заданий");
    }

    private void showTabCompleted() {
        activeTab = 1;
        resetTabs();
        tabCompleted.setBackgroundResource(R.drawable.bg_tasks_tab_selected);
        tabCompleted.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        recyclerTasks.setAdapter(completedAdapter);
        updateEmptyState(completedAdapter, "Нет выполненных заданий");
    }

    private void showTabOverdue() {
        activeTab = 2;
        resetTabs();
        tabOverdue.setBackgroundResource(R.drawable.bg_tasks_tab_selected);
        tabOverdue.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        recyclerTasks.setAdapter(overdueAdapter);
        updateEmptyState(overdueAdapter, "Нет просроченных заданий");
    }

    private void updateEmptyState(TasksAdapter adapter, String message) {
        boolean empty = adapter == null || adapter.getItemCount() == 0;
        if (tvEmpty != null) {
            tvEmpty.setText(message);
            tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        }
        recyclerTasks.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
