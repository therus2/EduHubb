package com.example.eduhub.school.attendance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.schedule.adapters.DataAdapter;
import com.example.eduhub.schedule.repository.ScheduleRepository;
import com.example.eduhub.school.attendance.repository.AttendanceRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class AttendanceFragment extends Fragment {

    private RecyclerView calendarWeek;
    private RecyclerView recyclerAttendance;
    private TextView textMonthYear;
    private TextView textPresentPercent;
    private TextView textAbsences;
    private TextView textLates;
    private DataAdapter dayAdapter;
    private AttendanceRepository repo;
    private String studentId;
    private int currentYear;
    private int currentMonth;
    private LocalDate selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            studentId = getArguments().getString("studentId", "");
        }
        return inflater.inflate(R.layout.fragment_attendance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        com.example.eduhub.appbars.BackHeaderFragment headerFragment =
                com.example.eduhub.appbars.BackHeaderFragment.newInstance("Посещаемость");
        getChildFragmentManager().beginTransaction()
                .replace(R.id.header_fragment_container, headerFragment)
                .commit();

        textMonthYear = view.findViewById(R.id.text_month_year);
        calendarWeek = view.findViewById(R.id.calendar_week);
        recyclerAttendance = view.findViewById(R.id.recycler_attendance);
        textPresentPercent = view.findViewById(R.id.text_present_percent);
        textAbsences = view.findViewById(R.id.text_absences);
        textLates = view.findViewById(R.id.text_lates);

        ImageButton prevBtn = view.findViewById(R.id.btn_prev_month);
        ImageButton nextBtn = view.findViewById(R.id.btn_next_month);

        LocalDate today = LocalDate.now();
        currentYear = today.getYear();
        currentMonth = today.getMonthValue();
        selectedDate = today;

        repo = new AttendanceRepository(requireContext(), studentId);

        prevBtn.setOnClickListener(v -> changeMonth(-1));
        nextBtn.setOnClickListener(v -> changeMonth(1));

        setupCalendar();
        loadAttendanceForSelectedDay();
        updateMonthlySummary();
    }

    private void setupCalendar() {
        List<com.example.eduhub.schedule.models.DataSchedule> days =
                ScheduleRepository.getDaysForMonth(currentYear, currentMonth);

        dayAdapter = new DataAdapter(requireContext(), days);
        dayAdapter.setOnDaySelectedListener(date -> {
            selectedDate = date;
            updateMonthYearLabel();
            loadAttendanceForSelectedDay();
        });
        dayAdapter.setSelectedDay(selectedDate);

        calendarWeek.setAdapter(dayAdapter);
        calendarWeek.scrollToPosition(dayAdapter.getSelectedWeekPosition());
        if (calendarWeek.getOnFlingListener() == null) {
            PagerSnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(calendarWeek);
        }
        updateMonthYearLabel();
    }

    private void changeMonth(int delta) {
        currentMonth += delta;
        if (currentMonth < 1) { currentMonth = 12; currentYear--; }
        else if (currentMonth > 12) { currentMonth = 1; currentYear++; }
        if (selectedDate != null) {
            int day = selectedDate.getDayOfMonth();
            int maxDay = YearMonth.of(currentYear, currentMonth).lengthOfMonth();
            if (day > maxDay) day = maxDay;
            selectedDate = LocalDate.of(currentYear, currentMonth, day);
        }
        setupCalendar();
        loadAttendanceForSelectedDay();
        updateMonthlySummary();
    }

    private void updateMonthYearLabel() {
        String monthName = ScheduleRepository.getMonthName(selectedDate.getMonthValue());
        textMonthYear.setText((monthName.substring(0, 1).toUpperCase() + monthName.substring(1))
                + " " + selectedDate.getYear());
    }

    private void loadAttendanceForSelectedDay() {
        if (selectedDate == null) return;

        List<com.example.eduhub.school.attendance.models.AttendanceItem> items =
                repo.getAttendanceItemsForDate(selectedDate);
        recyclerAttendance.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerAttendance.setAdapter(new AttendanceAdapter(items));

        View empty = getView() != null ? getView().findViewById(R.id.tv_attendance_empty) : null;
        if (empty != null) {
            empty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        }
        recyclerAttendance.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void updateMonthlySummary() {
        YearMonth currentMonthYm = YearMonth.from(selectedDate);
        int[] stats = repo.getMonthlySummary(currentMonthYm);

        int present = stats[0];
        int absent = stats[1];
        int late = stats[2];
        int total = present + absent + late;
        int percent = total > 0 ? (int) Math.round((present * 100.0) / total) : 0;

        textPresentPercent.setText(percent + "%");
        textAbsences.setText(String.valueOf(absent));
        textLates.setText(String.valueOf(late));
    }
}