package com.example.eduhub.school;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.eduhub.R;
import com.example.eduhub.appbars.ProfileNotificationFragment;
import com.example.eduhub.marks.models.MarkSubjectItem;
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.marks.repository.GradesRepository;
import com.example.eduhub.ui.SyncRefreshFragment;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

public class SchoolFragment extends SyncRefreshFragment {

    private String studentId;
    private String userId;
    private GradesRepository gradesRepository;

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
        return inflater.inflate(R.layout.fragment_school, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProfileNotificationFragment headerFragment = ProfileNotificationFragment.newInstance("Обучение", userId);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.header_fragment_container, headerFragment)
                .commit();

        gradesRepository = new GradesRepository(requireContext(), studentId);

        setupMenuClicks(view);
        loadChart(view);

        SwipeRefreshLayout swipe = view.findViewById(R.id.swipe_refresh);
        View schoolScroll = view.findViewById(R.id.school_scroll);
        if (swipe != null && schoolScroll != null) {
            bindRefreshScroll(swipe, schoolScroll);
        }
    }

    @Override
    protected void reloadAfterSync() {
        View v = getView();
        if (v != null) {
            gradesRepository = new GradesRepository(requireContext(), studentId);
            loadChart(v);
        }
    }

    private void setupMenuClicks(View view) {
        view.findViewById(R.id.card_attendance).setOnClickListener(v -> {
            Fragment fragment = new com.example.eduhub.school.attendance.AttendanceFragment();
            Bundle args = new Bundle();
            args.putString("studentId", studentId);
            fragment.setArguments(args);
            replaceFragment(fragment);
        });

        view.findViewById(R.id.card_announcements).setOnClickListener(v -> {
            Fragment fragment = new com.example.eduhub.school.announcements.AnnouncementsFragment();
            Bundle args = new Bundle();
            args.putString("studentId", studentId);
            args.putString("userId", userId);
            fragment.setArguments(args);
            replaceFragment(fragment);
        });
    }

    private void replaceFragment(Fragment fragment) {
        int destId;
        Bundle args = fragment.getArguments() != null ? fragment.getArguments() : new Bundle();
        if (fragment instanceof com.example.eduhub.school.attendance.AttendanceFragment) {
            destId = R.id.attendanceFragment;
        } else if (fragment instanceof com.example.eduhub.school.announcements.AnnouncementsFragment) {
            destId = R.id.announcementsFragment;
        } else {
            return;
        }
        Navigation.findNavController(requireView()).navigate(destId, args);
    }

    private void loadChart(View view) {
        LinearLayout barsContainer = view.findViewById(R.id.bars_container);
        TextView textEmpty = view.findViewById(R.id.text_chart_empty);

        List<MarkSubjectItem> subjects = gradesRepository.getSubjectAverages();
        barsContainer.removeAllViews();

        if (subjects == null || subjects.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
            return;
        }
        textEmpty.setVisibility(View.GONE);

        for (int i = 0; i < subjects.size(); i++) {
            barsContainer.addView(createBarRow(subjects.get(i)));

            if (i < subjects.size() - 1) {
                View divider = new View(requireContext());
                divider.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 1));
                divider.setBackgroundColor(Color.parseColor("#F0F0F0"));
                barsContainer.addView(divider);
            }
        }
    }

    private View createBarRow(MarkSubjectItem item) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, 10, 0, 10);

        TextView tvSubject = new TextView(requireContext());
        LinearLayout.LayoutParams subjParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.35f);
        tvSubject.setLayoutParams(subjParams);
        tvSubject.setText(item.getSubjectName());
        tvSubject.setTextColor(Color.parseColor("#333333"));
        tvSubject.setTextSize(12);
        tvSubject.setMaxLines(1);
        tvSubject.setEllipsize(android.text.TextUtils.TruncateAt.END);

        LinearLayout barBg = new LinearLayout(requireContext());
        LinearLayout.LayoutParams barBgParams = new LinearLayout.LayoutParams(
                0, 22, 0.50f);
        barBgParams.setMargins(8, 0, 8, 0);
        barBg.setLayoutParams(barBgParams);

        GradientDrawable bgShape = new GradientDrawable();
        bgShape.setShape(GradientDrawable.RECTANGLE);
        bgShape.setColor(Color.parseColor("#F4ECFF"));
        bgShape.setCornerRadius(11);
        barBg.setBackground(bgShape);

        View barFill = new View(requireContext());
        int barColor = getBarColor(item.getAverage());

        GradientDrawable fillShape = new GradientDrawable();
        fillShape.setShape(GradientDrawable.RECTANGLE);
        fillShape.setColor(barColor);
        fillShape.setCornerRadius(11);
        barFill.setBackground(fillShape);

        barBg.addView(barFill);
        barBg.post(() -> {
            int bgW = barBg.getWidth();
            if (bgW > 0) {
                int fillW = (int) (bgW * Math.min(item.getAverage() / 5.0, 1.0));
                barFill.getLayoutParams().width = fillW;
                barFill.requestLayout();
            }
        });

        TextView tvScore = new TextView(requireContext());
        LinearLayout.LayoutParams scoreParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.15f);
        tvScore.setLayoutParams(scoreParams);
        tvScore.setText(String.format(Locale.US, "%.1f", item.getAverage()));
        tvScore.setTextColor(barColor);
        tvScore.setTextSize(13);
        tvScore.setTypeface(null, android.graphics.Typeface.BOLD);

        row.addView(tvSubject);
        row.addView(barBg);
        row.addView(tvScore);

        return row;
    }

    private int getBarColor(double average) {
        if (average >= 4.5) return Color.parseColor("#4CAF50");
        if (average >= 3.5) return Color.parseColor("#9C27B0");
        if (average >= 3.0) return Color.parseColor("#FF9800");
        return Color.parseColor("#FF5252");
    }
}
