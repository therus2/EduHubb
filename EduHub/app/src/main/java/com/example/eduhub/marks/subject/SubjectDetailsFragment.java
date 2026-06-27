package com.example.eduhub.marks.subject;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.eduhub.R;
import com.example.eduhub.database.DBHelper;
import com.example.eduhub.marks.models.GradeDetailItem;
import com.example.eduhub.marks.repository.GradesRepository;
import com.example.eduhub.utils.LearningPeriodHelper;

import java.util.List;
import java.util.Locale;
import java.util.Locale;

public class SubjectDetailsFragment extends Fragment {

    private String studentId;
    private String subjectId;
    private String subjectName;
    private String teacherName;
    private float average;
    private int gradeCount;

    private TextView tvSubjectName;
    private TextView tvTeacherName;
    private TextView tvAverageValue;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private TextView tvProgressFraction;
    private LinearLayout gradesContainer;
    private TextView tvStatsGrades;
    private TextView tvStatsAbsences;
    private TextView tvStatsBest;

    private GradesRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            studentId = getArguments().getString("studentId", "");
            subjectId = getArguments().getString("subjectId", "");
            subjectName = getArguments().getString("subjectName", "");
            teacherName = getArguments().getString("teacherName", "");
            average = getArguments().getFloat("average", 0f);
            gradeCount = getArguments().getInt("gradeCount", 0);
        }
        return inflater.inflate(R.layout.fragment_subject_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        com.example.eduhub.appbars.BackHeaderFragment headerFragment =
                com.example.eduhub.appbars.BackHeaderFragment.newInstance(subjectName);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.header_fragment_container, headerFragment)
                .commit();

        repository = new GradesRepository(requireContext(), studentId);

        tvSubjectName = view.findViewById(R.id.text_subject_name);
        tvTeacherName = view.findViewById(R.id.text_teacher_name);
        tvAverageValue = view.findViewById(R.id.text_average_value);
        progressBar = view.findViewById(R.id.progress_bar);
        tvStatus = view.findViewById(R.id.text_status);
        tvProgressFraction = view.findViewById(R.id.text_progress_fraction);
        gradesContainer = view.findViewById(R.id.grades_container);
        tvStatsGrades = view.findViewById(R.id.text_stats_grades);
        tvStatsAbsences = view.findViewById(R.id.text_stats_absences);
        tvStatsBest = view.findViewById(R.id.text_stats_best);

        loadData();
    }

    private void loadData() {
        tvSubjectName.setText(subjectName);
        tvTeacherName.setText(teacherName);

        updatePeriodText();

        int progress = (int) Math.round(average / 5.0 * 100);
        tvAverageValue.setText(String.format(Locale.US, "%.2f", average));
        progressBar.setProgress(progress);

        if (gradeCount == 0) {
            tvStatus.setText("нет оценок");
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_gray));
        } else         if (gradeCount == 0) {
            tvStatus.setText("Нет оценок");
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_gray));
        } else if (average >= 4.55) {
            tvStatus.setText("цель достигнута ✓");
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_green));
        } else if (average >= 4.0) {
            tvStatus.setText("необходимо подтянуть");
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_gray));
        } else {
            tvStatus.setText("требуется особое внимание");
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.marks_text_red));
        }
        tvProgressFraction.setText(progress + " / 100");

        List<GradeDetailItem> grades = repository.getGradesBySubject(subjectId);
        int absenceCount = repository.getAbsenceCount(subjectId);

        tvStatsGrades.setText(String.valueOf(gradeCount));
        tvStatsAbsences.setText(String.valueOf(absenceCount));
        int best = 0;
        for (GradeDetailItem g : grades) {
            if (g.getValue() > best) best = g.getValue();
        }
        tvStatsBest.setText(best > 0 ? String.valueOf(best) : "—");

        gradesContainer.removeAllViews();
        for (GradeDetailItem grade : grades) {
            View gradeView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_subject_grade, gradesContainer, false);
            bindGradeView(gradeView, grade);
            gradesContainer.addView(gradeView);
        }
    }

    private void bindGradeView(View view, GradeDetailItem grade) {
        TextView tvValue = view.findViewById(R.id.text_grade_value);
        TextView tvType = view.findViewById(R.id.text_grade_type);
        TextView tvDate = view.findViewById(R.id.text_grade_date);
        TextView tvWeight = view.findViewById(R.id.text_grade_weight);
        TextView tvComment = view.findViewById(R.id.text_grade_comment);
        View expandedContent = view.findViewById(R.id.expanded_grade_content);
        View boxGrade = view.findViewById(R.id.box_grade);

        tvValue.setText(String.valueOf(grade.getValue()));
        tvType.setText(grade.getType());
        tvDate.setText(formatDate(grade.getCreatedAt()));
        tvWeight.setText("Вес: " + grade.getWeight());

        if (grade.getComment() != null && !grade.getComment().isEmpty()) {
            tvComment.setText(grade.getComment());
        } else {
            tvComment.setText("Нет комментария");
        }

        int color;
        if (grade.getValue() <= 3) {
            color = ContextCompat.getColor(requireContext(), R.color.marks_text_red);
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(Color.parseColor("#FFF0F0"));
            bg.setStroke(3, color);
            boxGrade.setBackground(bg);
            tvValue.setTextColor(color);
        } else {
            color = ContextCompat.getColor(requireContext(), R.color.marks_purple_main);
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(Color.parseColor("#F4ECFF"));
            bg.setStroke(3, color);
            boxGrade.setBackground(bg);
            tvValue.setTextColor(color);
        }

        view.setOnClickListener(v -> {
            boolean expanded = expandedContent.getVisibility() == View.VISIBLE;
            expandedContent.setVisibility(expanded ? View.GONE : View.VISIBLE);
        });
    }

    private void updatePeriodText() {
        DBHelper dbHelper = DBHelper.getInstance(requireContext());
        String system = dbHelper.getLearningSystem(studentId);
        dbHelper.close();

        String label = LearningPeriodHelper.getPeriodLabel(system);
        String dates = LearningPeriodHelper.getPeriodDates(system);

        TextView tvPeriodTitle = getView().findViewById(R.id.text_period_title);
        TextView tvPeriodDates = getView().findViewById(R.id.text_period_dates);

        if (tvPeriodTitle != null) {
            tvPeriodTitle.setText(label.toUpperCase());
        }
        if (tvPeriodDates != null) {
            tvPeriodDates.setText(dates);
        }
    }

    private String formatDate(String dateTime) {
        if (dateTime == null || dateTime.length() < 10) return "—";
        try {
            return dateTime.substring(8, 10) + "." + dateTime.substring(5, 7) + "." + dateTime.substring(0, 4);
        } catch (Exception e) {
            return dateTime;
        }
    }
}
