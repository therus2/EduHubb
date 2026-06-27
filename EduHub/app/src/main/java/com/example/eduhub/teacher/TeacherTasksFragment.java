package com.example.eduhub.teacher;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.teacher.models.AssignmentWithStatsItem;
import com.example.eduhub.teacher.repository.TeacherTasksRepository;
import com.example.eduhub.ui.SyncRefreshFragment;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class TeacherTasksFragment extends SyncRefreshFragment {

    private String teacherId;
    private TeacherTasksRepository repo;

    private TextView tvClassFilter;
    private TextView tvNoAssignments;
    private RecyclerView rvAssignments;

    private List<String> groupEntries = new ArrayList<>();
    private String selectedGroupId = null;
    private List<AssignmentWithStatsItem> allAssignments = new ArrayList<>();

    private AssignmentsAdapter assignmentsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_tasks, container, false);

        Bundle args = getArguments();
        teacherId = args != null ? args.getString("teacherId") : null;
        if (teacherId == null || teacherId.isEmpty()) {
            teacherId = UserSessionManager.getInstance(requireContext()).getTeacherId();
        }
        repo = new TeacherTasksRepository(requireContext(), teacherId);

        tvClassFilter = view.findViewById(R.id.tv_class_filter);
        tvNoAssignments = view.findViewById(R.id.tv_no_assignments);
        rvAssignments = view.findViewById(R.id.rv_assignments);

        rvAssignments.setLayoutManager(new LinearLayoutManager(getContext()));

        loadGroups();

        tvClassFilter.setOnClickListener(v -> showGroupSelector());

        View fabNewTask = view.findViewById(R.id.fab_new_task);
        if (fabNewTask != null) {
            fabNewTask.setOnClickListener(v -> openNewTask());
        }

        SwipeRefreshLayout swipe = view.findViewById(R.id.swipe_refresh);
        if (swipe != null) {
            bindRefreshList(swipe, rvAssignments);
        }

        return view;
    }

    @Override
    protected void reloadAfterSync() {
        loadAssignments();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAssignments();
    }

    private void openNewTask() {
        Bundle args = new Bundle();
        args.putString("teacherId", teacherId);
        Navigation.findNavController(requireView())
                .navigate(R.id.teacherNewTaskFragment, args);
    }

    private void loadGroups() {
        groupEntries = repo.getGroups();
        if (!groupEntries.isEmpty()) {
            if (selectedGroupId == null || !hasGroup(selectedGroupId)) {
                String first = groupEntries.get(0);
                selectedGroupId = first.split("\\|")[0];
            }
            String code = findGroupCode(selectedGroupId);
            tvClassFilter.setText(code + " класс");
            loadAssignments();
        }
    }

    private boolean hasGroup(String groupId) {
        for (String entry : groupEntries) {
            if (entry.split("\\|")[0].equals(groupId)) return true;
        }
        return false;
    }

    private String findGroupCode(String groupId) {
        for (String entry : groupEntries) {
            String[] parts = entry.split("\\|");
            if (parts[0].equals(groupId)) return parts[1];
        }
        return groupEntries.isEmpty() ? "" : groupEntries.get(0).split("\\|")[1];
    }

    private void showGroupSelector() {
        String[] names = new String[groupEntries.size()];
        for (int i = 0; i < groupEntries.size(); i++) {
            names[i] = groupEntries.get(i).split("\\|")[1] + " класс";
        }
        new AlertDialog.Builder(requireContext())
                .setTitle("Выберите класс")
                .setItems(names, (dialog, which) -> {
                    String entry = groupEntries.get(which);
                    selectedGroupId = entry.split("\\|")[0];
                    String code = entry.split("\\|")[1];
                    tvClassFilter.setText(code + " класс");
                    loadAssignments();
                })
                .show();
    }

    private void loadAssignments() {
        if (selectedGroupId == null) return;
        allAssignments = repo.getAssignmentsForGroup(selectedGroupId);

        if (allAssignments.isEmpty()) {
            tvNoAssignments.setVisibility(View.VISIBLE);
            tvNoAssignments.setText("Нет заданий для этого класса");
            rvAssignments.setVisibility(View.GONE);
            return;
        }

        tvNoAssignments.setVisibility(View.GONE);
        rvAssignments.setVisibility(View.VISIBLE);
        assignmentsAdapter = new AssignmentsAdapter(allAssignments);
        rvAssignments.setAdapter(assignmentsAdapter);
    }

    private void showSubmissionsForAssignment(AssignmentWithStatsItem assignment) {
        Bundle args = new Bundle();
        args.putString("assignmentId", assignment.getId());
        args.putString("groupId", selectedGroupId);
        args.putString("teacherId", teacherId);
        Navigation.findNavController(requireView())
                .navigate(R.id.teacherAssignmentDetailFragment, args);
    }


    private class AssignmentsAdapter extends RecyclerView.Adapter<AssignmentsAdapter.ViewHolder> {
        private final List<AssignmentWithStatsItem> data;

        AssignmentsAdapter(List<AssignmentWithStatsItem> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_assignment, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AssignmentWithStatsItem item = data.get(position);
            holder.tvSubjectBadge.setText(item.getSubjectName());
            holder.tvDueDate.setText(item.getDueDate() != null ? "до " + item.getDueDate() : "");
            holder.tvAssignmentTitle.setText(item.getTitle());
            holder.pbProgress.setProgress(item.getProgressPercent());
            holder.tvStats.setText(item.getSubmitted() + " из " + item.getTotal() + " сдали");
            holder.tvProgressPercent.setText(item.getProgressPercent() + "%");

            holder.itemView.setOnClickListener(v -> showSubmissionsForAssignment(item));
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSubjectBadge, tvDueDate, tvAssignmentTitle, tvStats, tvProgressPercent;
            ProgressBar pbProgress;

            ViewHolder(View v) {
                super(v);
                tvSubjectBadge = v.findViewById(R.id.tv_subject_badge);
                tvDueDate = v.findViewById(R.id.tv_due_date);
                tvAssignmentTitle = v.findViewById(R.id.tv_assignment_title);
                pbProgress = v.findViewById(R.id.pb_progress);
                tvStats = v.findViewById(R.id.tv_stats);
                tvProgressPercent = v.findViewById(R.id.tv_progress_percent);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (repo != null) repo.close();
    }
}
