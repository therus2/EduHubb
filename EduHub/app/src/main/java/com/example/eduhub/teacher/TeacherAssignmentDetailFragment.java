package com.example.eduhub.teacher;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.teacher.models.AssignmentWithStatsItem;
import com.example.eduhub.teacher.models.SubmissionItem;
import com.example.eduhub.teacher.repository.TeacherTasksRepository;
import com.example.eduhub.network.sync.DataSyncEvents;
import com.example.eduhub.network.sync.UserDataSyncManager;
import com.example.eduhub.network.util.ApiMapUtils;

import java.util.ArrayList;
import java.util.List;

public class TeacherAssignmentDetailFragment extends Fragment implements DataSyncEvents.Listener {

    private static final String ARG_ASSIGNMENT_ID = "assignmentId";
    private static final String ARG_GROUP_ID = "groupId";
    private static final String ARG_TEACHER_ID = "teacherId";

    private String assignmentId;
    private String groupId;
    private String teacherId;
    private TeacherTasksRepository repo;

    public static TeacherAssignmentDetailFragment newInstance(String assignmentId, String groupId, String teacherId) {
        TeacherAssignmentDetailFragment f = new TeacherAssignmentDetailFragment();
        Bundle b = new Bundle();
        b.putString(ARG_ASSIGNMENT_ID, assignmentId);
        b.putString(ARG_GROUP_ID, groupId);
        b.putString(ARG_TEACHER_ID, teacherId);
        f.setArguments(b);
        return f;
    }

    private TextView tvSubjectBadge, tvTitle, tvDueDate, tvDescription;
    private TextView tvTotalCount, tvPendingCount, tvSubmittedCount, tvOverdueCount, tvGradedCount;
    private LinearLayout submissionsTabs;
    private TextView tvTabAll, tvTabSubmitted, tvTabNotSubmitted, tvTabGraded;
    private RecyclerView rvSubmissions;

    private List<SubmissionItem> allSubmissions = new ArrayList<>();
    private int currentTab = 0;

    private String currentTitle;
    private String currentDueDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_assignment_detail, container, false);

        Bundle args = getArguments();
        if (args != null) {
            assignmentId = args.getString(ARG_ASSIGNMENT_ID);
            groupId = args.getString(ARG_GROUP_ID);
            groupId = ApiMapUtils.normalizeId(groupId);
            teacherId = args.getString(ARG_TEACHER_ID);
        }

        repo = new TeacherTasksRepository(requireContext(), teacherId);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        tvSubjectBadge = view.findViewById(R.id.tv_subject_badge);
        tvTitle = view.findViewById(R.id.tv_assignment_title);
        tvDueDate = view.findViewById(R.id.tv_due_date);
        tvDescription = view.findViewById(R.id.tv_description);

        tvTotalCount = view.findViewById(R.id.tv_total_count);
        tvPendingCount = view.findViewById(R.id.tv_pending_count);
        tvSubmittedCount = view.findViewById(R.id.tv_submitted_count);
        tvOverdueCount = view.findViewById(R.id.tv_overdue_count);
        tvGradedCount = view.findViewById(R.id.tv_graded_count);

        submissionsTabs = view.findViewById(R.id.submissions_tabs);
        tvTabAll = view.findViewById(R.id.tv_tab_all);
        tvTabSubmitted = view.findViewById(R.id.tv_tab_submitted);
        tvTabNotSubmitted = view.findViewById(R.id.tv_tab_not_submitted);
        tvTabGraded = view.findViewById(R.id.tv_tab_graded);

        rvSubmissions = view.findViewById(R.id.rv_submissions);
        rvSubmissions.setLayoutManager(new LinearLayoutManager(getContext()));

        tvTabAll.setOnClickListener(v -> setTab(0));
        tvTabSubmitted.setOnClickListener(v -> setTab(1));
        tvTabNotSubmitted.setOnClickListener(v -> setTab(2));
        tvTabGraded.setOnClickListener(v -> setTab(3));

        View btnEdit = view.findViewById(R.id.btn_edit_assignment);
        if (btnEdit != null) btnEdit.setOnClickListener(v -> showEditDialog());
        View btnDelete = view.findViewById(R.id.btn_delete_assignment);
        if (btnDelete != null) btnDelete.setOnClickListener(v -> showDeleteConfirm());

        loadData();
        syncGroupStudents();

        return view;
    }

    private void syncGroupStudents() {
        new Thread(() -> {
            try {
                UserDataSyncManager syncManager = new UserDataSyncManager(requireContext());
                syncManager.syncGroupStudents(groupId);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(this::loadData);
                }
            } catch (Exception e) {
                Log.w("TeacherAssignDetail", "syncGroupStudents failed", e);
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        DataSyncEvents.register(this);
        loadData();
    }

    @Override
    public void onPause() {
        DataSyncEvents.unregister(this);
        super.onPause();
    }

    @Override
    public void onBackgroundSyncComplete() {
        if (isAdded()) {
            loadData();
        }
    }

    private void showEditDialog() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, 0);

        final android.widget.EditText etTitle = new android.widget.EditText(requireContext());
        etTitle.setHint("Заголовок задания");
        etTitle.setText(currentTitle != null ? currentTitle : "");
        layout.addView(etTitle);

        final TextView tvPickDate = new TextView(requireContext());
        tvPickDate.setTextSize(16f);
        tvPickDate.setPadding(0, pad, 0, 0);
        final String[] due = {currentDueDate};
        tvPickDate.setText("Срок сдачи: " + (due[0] != null ? due[0] : "не задан"));
        tvPickDate.setOnClickListener(v -> {
            java.time.LocalDate base;
            try {
                base = java.time.LocalDate.parse(due[0]);
            } catch (Exception e) {
                base = java.time.LocalDate.now();
            }
            new android.app.DatePickerDialog(requireContext(), (picker, year, month, day) -> {
                due[0] = String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, day);
                tvPickDate.setText("Срок сдачи: " + due[0]);
            }, base.getYear(), base.getMonthValue() - 1, base.getDayOfMonth()).show();
        });
        layout.addView(tvPickDate);

        new AlertDialog.Builder(requireContext())
                .setTitle("Редактировать задание")
                .setView(layout)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String newTitle = etTitle.getText().toString().trim();
                    repo.updateAssignment(assignmentId,
                            newTitle.isEmpty() ? null : newTitle, due[0]);
                    refreshData();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showDeleteConfirm() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Удалить задание?")
                .setMessage("Задание и все его сдачи будут удалены.")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    repo.deleteAssignment(assignmentId);
                    getParentFragmentManager().popBackStack();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void loadData() {
        List<AssignmentWithStatsItem> assignments = repo.getAssignmentsForGroup(groupId);
        AssignmentWithStatsItem assignment = null;
        for (AssignmentWithStatsItem a : assignments) {
            if (a.getId().equals(assignmentId)) {
                assignment = a;
                break;
            }
        }

        if (assignment == null) return;

        currentTitle = assignment.getTitle();
        currentDueDate = assignment.getDueDate();
        tvSubjectBadge.setText(assignment.getSubjectName());
        tvTitle.setText(assignment.getTitle());
        tvDueDate.setText("Сдать до " + (assignment.getDueDate() != null ? assignment.getDueDate() : "—"));
        tvDescription.setText(assignment.getDescription());

        allSubmissions = repo.getSubmissionsForAssignment(assignmentId, groupId);
        Log.d("AssignmentDetail", "loadData: allSubmissions.size=" + allSubmissions.size()
                + " assignmentId=" + assignmentId + " groupId=" + groupId);

        int total = allSubmissions.size();
        int submitted = 0, completed = 0, notStarted = 0;
        for (SubmissionItem s : allSubmissions) {
            switch (s.getStatus()) {
                case "SUBMITTED": submitted++; break;
                case "COMPLETED": completed++; break;
                default: notStarted++; break;
            }
        }
        int pendingReview = submitted;

        tvTotalCount.setText(String.valueOf(total));
        tvPendingCount.setText(String.valueOf(pendingReview));
        tvSubmittedCount.setText(String.valueOf(submitted));
        tvOverdueCount.setText(String.valueOf(notStarted));
        tvGradedCount.setText(String.valueOf(completed));

        setTab(0);
    }

    private void setTab(int tabIndex) {
        currentTab = tabIndex;

        TextView[] tabs = {tvTabAll, tvTabSubmitted, tvTabNotSubmitted, tvTabGraded};
        for (TextView t : tabs) {
            t.setBackgroundResource(android.R.color.transparent);
            t.setTextColor(requireContext().getColor(R.color.text_gray_secondary));
        }
        if (tabIndex >= 0 && tabIndex < tabs.length) {
            tabs[tabIndex].setBackgroundResource(R.drawable.bg_rounded_white);
            tabs[tabIndex].setTextColor(requireContext().getColor(R.color.black));
        }

        List<SubmissionItem> filtered = new ArrayList<>();
        for (SubmissionItem s : allSubmissions) {
            switch (tabIndex) {
                case 0: filtered.add(s); break;
                case 1: if ("SUBMITTED".equals(s.getStatus())) filtered.add(s); break;
                case 2: if ("NOT_STARTED".equals(s.getStatus()) || "IN_PROGRESS".equals(s.getStatus())) filtered.add(s); break;
                case 3: if ("COMPLETED".equals(s.getStatus())) filtered.add(s); break;
            }
        }

        rvSubmissions.setAdapter(new SubmissionsAdapter(filtered));
    }

    private class SubmissionsAdapter extends RecyclerView.Adapter<SubmissionsAdapter.ViewHolder> {
        private final List<SubmissionItem> data;

        SubmissionsAdapter(List<SubmissionItem> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_submission, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SubmissionItem item = data.get(position);
            holder.tvStudentName.setText(item.getStudentName());

            String studentComment = item.getStudentComment();
            if (studentComment != null && !studentComment.isEmpty()) {
                holder.tvStudentComment.setVisibility(View.VISIBLE);
                holder.tvStudentComment.setText("Комментарий: " + studentComment);
            } else {
                holder.tvStudentComment.setVisibility(View.GONE);
            }

            if ("SUBMITTED".equals(item.getStatus())) {
                holder.tvSubmissionTime.setVisibility(View.VISIBLE);
                holder.tvSubmissionTime.setText(item.getSubmissionTime());
                holder.tvSubmissionStatus.setVisibility(View.GONE);
            } else if ("COMPLETED".equals(item.getStatus())) {
                holder.tvSubmissionTime.setVisibility(View.VISIBLE);
                holder.tvSubmissionTime.setText("Проверено — оценка " + (item.getReceivedPoints() != null ? item.getReceivedPoints() : "—"));
                holder.tvSubmissionStatus.setVisibility(View.GONE);
            } else {
                holder.tvSubmissionTime.setVisibility(View.GONE);
                holder.tvSubmissionStatus.setVisibility(View.VISIBLE);
                holder.tvSubmissionStatus.setText("Не сдано");
            }

            holder.itemView.setOnClickListener(v -> {
                if ("SUBMITTED".equals(item.getStatus())) {
                    showGradeDialog(item);
                }
            });
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvStudentName, tvSubmissionTime, tvSubmissionStatus, tvStudentComment;

            ViewHolder(View v) {
                super(v);
                tvStudentName = v.findViewById(R.id.tv_student_name);
                tvSubmissionTime = v.findViewById(R.id.tv_submission_time);
                tvSubmissionStatus = v.findViewById(R.id.tv_submission_status);
                tvStudentComment = v.findViewById(R.id.tv_student_comment);
            }
        }
    }

    private void showGradeDialog(SubmissionItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Проверка работы");
        builder.setMessage(item.getStudentName() + "\n" + item.getSubmissionTime());

        View view = getLayoutInflater().inflate(R.layout.dialog_grade_submission, null);
        TextView tvSelectedGrade = view.findViewById(R.id.tv_selected_grade);
        TextView btn2 = view.findViewById(R.id.btn_grade_2);
        TextView btn3 = view.findViewById(R.id.btn_grade_3);
        TextView btn4 = view.findViewById(R.id.btn_grade_4);
        TextView btn5 = view.findViewById(R.id.btn_grade_5);

        final int[] selectedGrade = {0};

        View.OnClickListener gradeListener = v -> {
            int grade = Integer.parseInt(((TextView) v).getText().toString());
            selectedGrade[0] = grade;
            tvSelectedGrade.setText("Выбрана оценка: " + grade);
            
            btn2.setAlpha(0.5f); btn3.setAlpha(0.5f); btn4.setAlpha(0.5f); btn5.setAlpha(0.5f);
            v.setAlpha(1.0f);
        };
        btn2.setOnClickListener(gradeListener);
        btn3.setOnClickListener(gradeListener);
        btn4.setOnClickListener(gradeListener);
        btn5.setOnClickListener(gradeListener);

        builder.setView(view);

        builder.setPositiveButton("Принять", (dialog, which) -> {
            int grade = selectedGrade[0];
            if (grade < 2) grade = 3;
            repo.gradeSubmissionWithGrade(
                    item.getCompletionId(),
                    item.getStudentId(),
                    assignmentId,
                    grade,
                    "Проверено учителем"
            );
            refreshData();
        });

        builder.setNegativeButton("Принять без оценки", (dialog, which) -> {
            repo.gradeSubmission(item.getCompletionId(), "COMPLETED", 0);
            refreshData();
        });

        builder.setNeutralButton("Отмена", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void refreshData() {
        allSubmissions = repo.getSubmissionsForAssignment(assignmentId, groupId);
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (repo != null) repo.close();
    }
}
