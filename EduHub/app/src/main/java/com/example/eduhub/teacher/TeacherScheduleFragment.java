package com.example.eduhub.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.appbars.ProfileNotificationFragment;
import com.example.eduhub.R;
import com.example.eduhub.database.DBHelper;
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.schedule.adapters.DataAdapter;
import com.example.eduhub.schedule.adapters.ScheduleListAdapter;
import com.example.eduhub.schedule.models.ScheduleItem;
import com.example.eduhub.schedule.repository.ScheduleRepository;
import com.example.eduhub.ui.SyncRefreshFragment;

import androidx.navigation.Navigation;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.time.LocalDate;
import java.util.List;

public class TeacherScheduleFragment extends SyncRefreshFragment {

    private String teacherId;
    private int currentYear;
    private int currentMonth;
    private LocalDate selectedDate;
    private DBHelper dbHelper;

    private TextView monthTextView;
    private DataAdapter calendarAdapter;
    private ScheduleListAdapter lessonsAdapter;
    private RecyclerView lessonsRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        teacherId = args != null ? args.getString("teacherId") : null;
        if (teacherId == null || teacherId.isEmpty()) {
            teacherId = UserSessionManager.getInstance(requireContext()).getTeacherId();
        }
        dbHelper = DBHelper.getInstance(requireContext());

        String userId = UserSessionManager.getInstance(requireContext()).getUserId();
        if (userId == null || userId.isEmpty()) {
            userId = teacherId;
        }

        ProfileNotificationFragment headerFragment = ProfileNotificationFragment.newInstance("Расписание", userId);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.header_fragment_container, headerFragment)
                .commit();

        LocalDate today = LocalDate.now();
        currentYear = today.getYear();
        currentMonth = today.getMonthValue();
        selectedDate = today;

        monthTextView = view.findViewById(R.id.text_view_month);

        ImageButton prevBtn = view.findViewById(R.id.btn_prev_month);
        ImageButton nextBtn = view.findViewById(R.id.btn_next_month);

        prevBtn.setOnClickListener(v -> changeMonth(-1));
        nextBtn.setOnClickListener(v -> changeMonth(1));

        setupCalendar(view);
        setupLessons(view);

        SwipeRefreshLayout swipe = view.findViewById(R.id.swipe_refresh);
        if (swipe != null && lessonsRecyclerView != null) {
            bindRefreshList(swipe, lessonsRecyclerView);
        }
    }

    @Override
    protected void reloadAfterSync() {
        loadLessonsForSelectedDay();
    }

    private void navigateToDate(LocalDate date) {
        selectedDate = date;
        int newYear = date.getYear();
        int newMonth = date.getMonthValue();

        if (newYear != currentYear || newMonth != currentMonth) {
            currentYear = newYear;
            currentMonth = newMonth;
            updateCalendar();
        }
        updateMonthLabel();
        loadLessonsForSelectedDay();
    }

    private void changeMonth(int delta) {
        currentMonth += delta;
        if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        } else if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }

        if (selectedDate != null) {
            int day = selectedDate.getDayOfMonth();
            int maxDay = java.time.YearMonth.of(currentYear, currentMonth).lengthOfMonth();
            if (day > maxDay) day = maxDay;
            selectedDate = LocalDate.of(currentYear, currentMonth, day);
        }

        updateCalendar();
        loadLessonsForSelectedDay();
    }

    private void setupCalendar(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_data_schedule);
        calendarAdapter = new DataAdapter(requireContext(), ScheduleRepository.getDaysForMonth(currentYear, currentMonth));
        calendarAdapter.setOnDaySelectedListener(this::navigateToDate);
        calendarAdapter.setSelectedDay(selectedDate);
        recyclerView.setAdapter(calendarAdapter);
        recyclerView.scrollToPosition(calendarAdapter.getSelectedWeekPosition());

        if (recyclerView.getOnFlingListener() == null) {
            PagerSnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);
        }

        updateMonthLabel();
    }

    private void updateCalendar() {
        RecyclerView recyclerView = requireView().findViewById(R.id.recycler_view_data_schedule);
        calendarAdapter = new DataAdapter(requireContext(), ScheduleRepository.getDaysForMonth(currentYear, currentMonth));
        calendarAdapter.setOnDaySelectedListener(this::navigateToDate);
        calendarAdapter.setSelectedDay(selectedDate);
        recyclerView.setAdapter(calendarAdapter);
        recyclerView.scrollToPosition(calendarAdapter.getSelectedWeekPosition());
        updateMonthLabel();
    }

    private void updateMonthLabel() {
        String monthName = ScheduleRepository.getMonthName(selectedDate.getMonthValue());
        monthTextView.setText(monthName + " " + selectedDate.getYear());
    }

    private void setupLessons(View view) {
        lessonsRecyclerView = view.findViewById(R.id.recycler_view_lessons);
        loadLessonsForSelectedDay();
    }

    private void loadLessonsForSelectedDay() {
        if (selectedDate == null) return;
        List<ScheduleItem> items = ScheduleRepository.getLessonsForTeacherDay(dbHelper, teacherId, selectedDate);
        if (items.isEmpty()) {
            lessonsRecyclerView.setVisibility(View.GONE);
            requireView().findViewById(R.id.text_empty_schedule).setVisibility(View.VISIBLE);
        } else {
            lessonsRecyclerView.setVisibility(View.VISIBLE);
            requireView().findViewById(R.id.text_empty_schedule).setVisibility(View.GONE);
            lessonsAdapter = new ScheduleListAdapter(items);
            String tid = teacherId;
            lessonsAdapter.setOnLessonClickListener((lessonId, title, info) -> {
                Bundle args = new Bundle();
                args.putString("lessonId", lessonId);
                args.putString("date", selectedDate.toString());
                args.putString("teacherId", tid);
                Navigation.findNavController(requireView())
                        .navigate(R.id.teacherLessonDetailFragment, args);
            });
            lessonsRecyclerView.setAdapter(lessonsAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLessonsForSelectedDay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDatabase();
        }
    }
}
