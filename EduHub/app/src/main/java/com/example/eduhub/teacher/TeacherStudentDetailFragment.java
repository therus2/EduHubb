package com.example.eduhub.teacher;

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
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.network.sync.UserDataSyncManager;
import com.example.eduhub.teacher.models.AttendanceDetailItem;
import com.example.eduhub.teacher.models.GradeDetailItem;
import com.example.eduhub.teacher.repository.TeacherStudentRepository;

import java.util.List;

public class TeacherStudentDetailFragment extends Fragment {

    private String studentId;
    private String studentName;
    private String groupName;
    private String teacherId;

    private TeacherStudentRepository repository;
    private RecyclerView rvContent;
    private TextView tvEmpty;
    private TextView tabGrades, tabAttendance;
    private boolean showingGrades = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_student_detail, container, false);

        Bundle args = getArguments();
        if (args != null) {
            studentId = args.getString("studentId");
            studentName = args.getString("studentName", "");
            groupName = args.getString("groupName", "");
            teacherId = args.getString("teacherId");
        }
        if (teacherId == null || teacherId.isEmpty()) {
            teacherId = UserSessionManager.getInstance(requireContext()).getTeacherId();
        }

        repository = new TeacherStudentRepository(requireContext());

        TextView tvName = view.findViewById(R.id.tv_student_name);
        tvName.setText(studentName);

        TextView tvGroup = view.findViewById(R.id.tv_group_name);
        tvGroup.setText(groupName);

        tabGrades = view.findViewById(R.id.tab_grades);
        tabAttendance = view.findViewById(R.id.tab_attendance);
        rvContent = view.findViewById(R.id.rv_content);
        tvEmpty = view.findViewById(R.id.tv_empty);

        tabGrades.setOnClickListener(v -> setGradesTab(true));
        tabAttendance.setOnClickListener(v -> setGradesTab(false));

        View btnBack = view.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        setGradesTab(true);

        syncStudentData();

        return view;
    }

    private void syncStudentData() {
        new Thread(() -> {
            try {
                UserDataSyncManager syncManager = new UserDataSyncManager(requireContext());
                syncManager.syncStudentData(studentId);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (showingGrades) showGradesList();
                        else showAttendanceList();
                    });
                }
            } catch (Exception e) {
                Log.w("TeacherStudentDetail", "syncStudentData failed", e);
            }
        }).start();
    }

    private void setGradesTab(boolean showGrades) {
        showingGrades = showGrades;
        int blue = ContextCompat.getColor(requireContext(), R.color.link_blue);
        int gray = ContextCompat.getColor(requireContext(), R.color.marks_text_gray);

        if (showGrades) {
            tabGrades.setBackgroundResource(R.drawable.bg_rounded_white);
            tabGrades.setTextColor(blue);
            tabAttendance.setBackgroundResource(android.R.color.transparent);
            tabAttendance.setTextColor(gray);
            showGradesList();
        } else {
            tabAttendance.setBackgroundResource(R.drawable.bg_rounded_white);
            tabAttendance.setTextColor(blue);
            tabGrades.setBackgroundResource(android.R.color.transparent);
            tabGrades.setTextColor(gray);
            showAttendanceList();
        }
    }

    private void showGradesList() {
        List<GradeDetailItem> grades = repository.getGrades(studentId);
        if (grades.isEmpty()) {
            rvContent.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvContent.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
            rvContent.setAdapter(new GradesAdapter(grades));
        }
    }

    private void showAttendanceList() {
        List<AttendanceDetailItem> attendance = repository.getAttendance(studentId);
        if (attendance.isEmpty()) {
            rvContent.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvContent.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
            rvContent.setAdapter(new AttendanceAdapter(attendance));
        }
    }


    private static class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.ViewHolder> {
        private final List<GradeDetailItem> data;

        GradesAdapter(List<GradeDetailItem> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_grade_detail, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GradeDetailItem item = data.get(position);
            holder.tvGradeValue.setText(String.valueOf(item.getValue()));
            holder.tvSubjectName.setText(item.getSubjectName());
            holder.tvGradeInfo.setText(item.getType() + " • " + (item.getDate() != null ? item.getDate() : ""));
            holder.tvComment.setText(item.getComment() != null && !item.getComment().isEmpty() ? item.getComment() : "");
        }

        @Override
        public int getItemCount() { return data.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvGradeValue, tvSubjectName, tvGradeInfo, tvComment;
            ViewHolder(View v) {
                super(v);
                tvGradeValue = v.findViewById(R.id.tv_grade_value);
                tvSubjectName = v.findViewById(R.id.tv_subject_name);
                tvGradeInfo = v.findViewById(R.id.tv_grade_info);
                tvComment = v.findViewById(R.id.tv_comment);
            }
        }
    }


    private static class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
        private final List<AttendanceDetailItem> data;

        AttendanceAdapter(List<AttendanceDetailItem> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_attendance_detail, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AttendanceDetailItem item = data.get(position);
            holder.tvDate.setText(item.getDate() != null ? item.getDate() : "");
            holder.tvTopic.setText(item.getLessonTopic() != null ? item.getLessonTopic() : "");

            String status = item.getStatus() != null ? item.getStatus() : "";
            switch (status) {
                case "PRESENT":
                    holder.tvStatusIcon.setText("+");
                    holder.tvStatusIcon.setTextColor(0xFF4CAF50);
                    holder.tvStatusText.setText("Присутствовал");
                    holder.tvStatusText.setTextColor(0xFF4CAF50);
                    break;
                case "ABSENT":
                    holder.tvStatusIcon.setText("—");
                    holder.tvStatusIcon.setTextColor(0xFFF44336);
                    holder.tvStatusText.setText("Отсутствовал");
                    holder.tvStatusText.setTextColor(0xFFF44336);
                    break;
                case "LATE":
                    holder.tvStatusIcon.setText("~");
                    holder.tvStatusIcon.setTextColor(0xFFFF9800);
                    holder.tvStatusText.setText("Опоздал");
                    holder.tvStatusText.setTextColor(0xFFFF9800);
                    break;
                case "EXCUSED_ABSENT":
                    holder.tvStatusIcon.setText("У");
                    holder.tvStatusIcon.setTextColor(0xFF2196F3);
                    holder.tvStatusText.setText("Уважительная");
                    holder.tvStatusText.setTextColor(0xFF2196F3);
                    break;
                default:
                    holder.tvStatusIcon.setText("?");
                    holder.tvStatusIcon.setTextColor(0xFF9E9E9E);
                    holder.tvStatusText.setText(status);
                    holder.tvStatusText.setTextColor(0xFF9E9E9E);
                    break;
            }
        }

        @Override
        public int getItemCount() { return data.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvStatusIcon, tvDate, tvTopic, tvStatusText;
            ViewHolder(View v) {
                super(v);
                tvStatusIcon = v.findViewById(R.id.tv_status_icon);
                tvDate = v.findViewById(R.id.tv_att_date);
                tvTopic = v.findViewById(R.id.tv_lesson_topic);
                tvStatusText = v.findViewById(R.id.tv_status_text);
            }
        }
    }
}
