package com.example.eduhub.teacher;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.database.DBHelper;
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.ui.SyncAwareFragment;
import com.example.eduhub.teacher.models.JournalStudentItem;
import com.example.eduhub.teacher.repository.TeacherJournalDetailRepository;
import com.example.eduhub.utils.LearningPeriodHelper;
import com.google.android.material.snackbar.Snackbar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TeacherJournalDetailFragment extends SyncAwareFragment {

    private static final String DEFAULT_GRADE = "4";
    private static final String[] DAY_NAMES = {"", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
    private static final int SNACKBAR_DURATION = 3000;

    private String groupId;
    private String groupName;
    private String subjectId;
    private String subjectName;
    private String teacherId;

    private TeacherJournalDetailRepository detailRepo;
    private DBHelper dbHelper;
    private List<TeacherJournalDetailRepository.LessonDayInfo> lessonDays;
    private List<String> weekDates;
    private List<String> weekFullDates;
    private List<String> weekLessonIds;
    private List<JournalStudentItem> students;
    private StudentsAdapter studentsAdapter;
    private TextView tvWeekLabel, tvStatus;
    private View vStatusDot;

    private LocalDate currentWeekStart;

    private HorizontalScrollView hsvDatesHeader;
    private LinearLayout llDatesContainer;
    private RecyclerView rvStudents;

    private String lastActionStudentId;
    private String lastActionLessonId;
    private String lastActionDate;
    private String lastActionOldGradeId;
    private String lastActionOldValue;
    private String lastActionNewValue;

    private View rootView;
    private LocalDate periodStart, periodEnd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_teacher_journal_detail, container, false);

        Bundle args = getArguments();
        if (args != null) {
            
            
            groupId = args.getString("groupId");
            groupName = args.getString("groupName", "?");
            subjectId = args.getString("subjectId");
            subjectName = args.getString("subjectName", "?");
            
            teacherId = args.getString("teacherId");
        }
        
        
        if (teacherId == null || teacherId.isEmpty()) {
            teacherId = UserSessionManager.getInstance(requireContext()).getTeacherId();
        }

        detailRepo = new TeacherJournalDetailRepository(requireContext(), groupId, subjectId);
        dbHelper = DBHelper.getInstance(requireContext());

        setupViews();
        loadData();

        return rootView;
    }

    @Override
    protected void reloadAfterSync() {
        loadData();
    }

    private void setupViews() {
        TextView tvTitle = rootView.findViewById(R.id.tv_journal_title);
        tvTitle.setText(groupName + " \u2022 " + subjectName);

        String system = dbHelper.getLearningSystem(teacherId);
        TextView tvPeriod = rootView.findViewById(R.id.tv_period_label);
        if (tvPeriod != null) {
            tvPeriod.setText(LearningPeriodHelper.getPeriodLabel(system));
        }

        tvStatus = rootView.findViewById(R.id.tv_status);
        vStatusDot = rootView.findViewById(R.id.v_status_dot);
        tvWeekLabel = rootView.findViewById(R.id.tv_week_label);

        hsvDatesHeader = rootView.findViewById(R.id.hsv_dates_header);
        llDatesContainer = rootView.findViewById(R.id.ll_dates_container);

        rvStudents = rootView.findViewById(R.id.rv_students);
        rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));

        rootView.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().onBackPressed());

        rootView.findViewById(R.id.btn_prev_week).setOnClickListener(v -> {
            currentWeekStart = currentWeekStart.minusDays(7);
            loadData();
            hsvDatesHeader.smoothScrollTo(0, 0);
        });

        rootView.findViewById(R.id.btn_next_week).setOnClickListener(v -> {
            currentWeekStart = currentWeekStart.plusDays(7);
            loadData();
            hsvDatesHeader.smoothScrollTo(0, 0);
        });
    }

    private void loadData() {
        if (currentWeekStart == null) {
            currentWeekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        }
        lessonDays = detailRepo.getLessonDaysForWeek(currentWeekStart);
        weekDates = detailRepo.getWeekDates(currentWeekStart, lessonDays);
        weekFullDates = detailRepo.getWeekFullDates(currentWeekStart, lessonDays);
        weekLessonIds = detailRepo.getLessonIdsForWeek(lessonDays);
        String system = dbHelper.getLearningSystem(teacherId);
        periodStart = LearningPeriodHelper.getPeriodStart(system);
        periodEnd = LearningPeriodHelper.getPeriodEnd(system);
        students = detailRepo.getStudentsForWeek(currentWeekStart, weekFullDates, weekLessonIds, periodStart, periodEnd);

        String weekEndStr = currentWeekStart.plusDays(6).format(DateTimeFormatter.ofPattern("dd.MM"));
        tvWeekLabel.setText(currentWeekStart.format(DateTimeFormatter.ofPattern("dd.MM")) + " \u2013 " + weekEndStr);

        buildDateHeaders();

        if (studentsAdapter == null) {
            studentsAdapter = new StudentsAdapter(students);
            rvStudents.setAdapter(studentsAdapter);
        } else {
            studentsAdapter.updateData(students);
        }

        setStatusSaved();
    }

    private void buildDateHeaders() {
        llDatesContainer.removeAllViews();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < weekDates.size(); i++) {
            View dateView = getLayoutInflater().inflate(R.layout.item_teacher_journal_date, llDatesContainer, false);
            TextView tvDate = dateView.findViewById(R.id.tv_date);
            TextView tvDayName = dateView.findViewById(R.id.tv_day_name);
            View todayDot = dateView.findViewById(R.id.v_today_dot);

            String[] parts = weekDates.get(i).split("\\.");
            String dayMonth = weekDates.get(i);

            tvDate.setText(dayMonth);
            tvDayName.setText(DAY_NAMES[lessonDays.get(i).dayOfWeek]);

            LocalDate date = currentWeekStart.with(DayOfWeek.of(lessonDays.get(i).dayOfWeek));
            if (date.equals(today)) {
                LinearLayout container = (LinearLayout) dateView;
                container.setBackgroundResource(R.drawable.bg_date_today);
                container.setPadding(8, 4, 8, 4);
                tvDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.today_text));
                todayDot.setVisibility(View.VISIBLE);
            }

            llDatesContainer.addView(dateView);
        }
    }


    private void setStatusSaved() {
        tvStatus.setText("Сохранено");
        vStatusDot.setBackgroundTintList(
                ContextCompat.getColorStateList(requireContext(), R.color.marks_text_green));
        tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray_secondary));
    }


    private void onGradeCellClick(int studentIndex, int dateIndex) {
        showGradePicker(studentIndex, dateIndex);
    }

    private void onGradeCellLongClick(int studentIndex, int dateIndex) {
        showGradePicker(studentIndex, dateIndex);
    }

    private void showGradePicker(int studentIndex, int dateIndex) {
        JournalStudentItem student = students.get(studentIndex);
        String studentName = student.getStudentName();
        String[] parts = studentName.split(" ", 2);
        String shortName = parts.length > 0 ? parts[0] : studentName;
        String dateLabel = weekDates.get(dateIndex);
        boolean hasGrade = !student.getMarks()[dateIndex].isEmpty();

        GradePickerBottomSheet bottomSheet = GradePickerBottomSheet.newInstance(
                shortName, dateLabel, studentIndex, dateIndex, hasGrade
        );
        bottomSheet.setListener(new GradePickerBottomSheet.GradePickerListener() {
            @Override
            public void onGradeSelected(int si, int di, String value, String gradeType) {
                setGradeForCell(si, di, value, gradeType);
            }

            @Override
            public void onGradeDeleted(int si, int di) {
                deleteGrade(si, di);
            }
        });
        bottomSheet.show(getChildFragmentManager(), "GradePicker");
    }

    private void setGradeForCell(int studentIndex, int dateIndex, String value) {
        setGradeForCell(studentIndex, dateIndex, value, "CURRENT");
    }

    private void setGradeForCell(int studentIndex, int dateIndex, String value, String gradeType) {
        if (gradeType == null || gradeType.isEmpty()) gradeType = "CURRENT";
        JournalStudentItem student = students.get(studentIndex);
        String oldGradeId = student.getGradeIds()[dateIndex];
        String lessonId = weekLessonIds.get(dateIndex);
        String fullDate = weekFullDates.get(dateIndex);
        String studentId = student.getStudentId();

        saveForUndo(studentId, lessonId, fullDate, oldGradeId, student.getMarks()[dateIndex], value);

        boolean wasAttendance = oldGradeId != null && oldGradeId.startsWith("att:");
        boolean isAttendance = value.equals("\u041D") || value.equals("\u0411");

        if (isAttendance) {
            if (!wasAttendance && oldGradeId != null) {
                dbHelper.deleteGradeById(oldGradeId);
            }
            String status = value.equals("\u041D") ? "ABSENT" : "EXCUSED_ABSENT";
            dbHelper.upsertAttendance(lessonId, fullDate, studentId, status, "", teacherId);
            String subjectName = dbHelper.findSubjectNameById(subjectId);
            if (subjectName == null) subjectName = "Предмет";
            String notifMsg = value.equals("\u041D")
                    ? subjectName + ": пропуск (отсутствовал)"
                    : subjectName + ": пропуск (уважительная причина)";
            dbHelper.insertNotification(studentId, "Посещаемость", notifMsg, "ATTENDANCE");
        } else {
            if (wasAttendance) {
                dbHelper.deleteAttendanceByLessonAndDate(studentId, lessonId, fullDate);
            }
            int gradeValue = Integer.parseInt(value);
            if (oldGradeId != null && !wasAttendance) {
                dbHelper.updateGradeValue(oldGradeId, gradeValue);
                dbHelper.updateGradeType(oldGradeId, gradeType);
            } else {
                dbHelper.insertGradeForLesson(studentId, subjectId,
                        teacherId, gradeValue, gradeType, "", lessonId, fullDate);
            }
        }

        reloadAndAnimate(studentIndex, dateIndex, value);
        showUndo();
    }

    private void deleteGrade(int studentIndex, int dateIndex) {
        JournalStudentItem student = students.get(studentIndex);
        String gradeId = student.getGradeIds()[dateIndex];

        if (gradeId != null) {
            saveForUndo(student.getStudentId(), weekLessonIds.get(dateIndex),
                    weekFullDates.get(dateIndex), gradeId, student.getMarks()[dateIndex], "");
            if (gradeId.startsWith("att:")) {
                dbHelper.deleteAttendanceByLessonAndDate(
                        student.getStudentId(), weekLessonIds.get(dateIndex), weekFullDates.get(dateIndex));
            } else {
                dbHelper.deleteGradeById(gradeId);
            }
            reloadStudent(studentIndex);
        }
    }


    private void saveForUndo(String studentId, String lessonId, String date,
                             String oldGradeId, String oldValue, String newValue) {
        this.lastActionStudentId = studentId;
        this.lastActionLessonId = lessonId;
        this.lastActionDate = date;
        this.lastActionOldGradeId = oldGradeId;
        this.lastActionOldValue = oldValue;
        this.lastActionNewValue = newValue;
    }

    private void showUndo() {
        if (!isAdded()) return;

        Snackbar snackbar = Snackbar.make(rootView, "Оценка выставлена", SNACKBAR_DURATION);
        snackbar.setAction("Отменить", v -> performUndo());
        snackbar.setTextColor(ContextCompat.getColor(requireContext(), R.color.snackbar_text));
        snackbar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.snackbar_action));
        snackbar.getView().setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.snackbar_bg));
        snackbar.show();
    }

    private void performUndo() {
        if (lastActionOldGradeId != null && !lastActionOldValue.isEmpty()) {
            if (lastActionOldGradeId.startsWith("att:")) {
                String status = lastActionOldValue.equals("\u041D") ? "ABSENT" : "EXCUSED_ABSENT";
                dbHelper.upsertAttendance(lastActionLessonId, lastActionDate,
                        lastActionStudentId, status, "", teacherId);
            } else {
                dbHelper.updateGradeValue(lastActionOldGradeId, Integer.parseInt(lastActionOldValue));
                dbHelper.updateGradeType(lastActionOldGradeId, "CURRENT");
            }
        } else if (lastActionOldGradeId != null && lastActionOldValue.isEmpty()) {
            if (lastActionOldGradeId.startsWith("att:")) {
                dbHelper.upsertAttendance(lastActionLessonId, lastActionDate,
                        lastActionStudentId, "ABSENT", "", teacherId);
            } else {
                dbHelper.deleteGradeById(lastActionOldGradeId);
            }
        } else {
            if (!lastActionNewValue.isEmpty()) {
                boolean isAttendance = lastActionNewValue.equals("\u041D") || lastActionNewValue.equals("\u0411");
                if (isAttendance) {
                    dbHelper.deleteAttendanceByLessonAndDate(
                            lastActionStudentId, lastActionLessonId, lastActionDate);
                } else {
                    dbHelper.insertGradeForLesson(lastActionStudentId, subjectId,
                            teacherId, Integer.parseInt(lastActionNewValue), "CURRENT", "", lastActionLessonId, lastActionDate);
                }
            }
        }

        reloadAll();
        if (!isAdded()) return;
        Snackbar.make(rootView, "Отменено", 1500).show();
    }


    private void reloadStudent(int studentIndex) {
        students = detailRepo.getStudentsForWeek(currentWeekStart, weekFullDates, weekLessonIds, periodStart, periodEnd);
        studentsAdapter.updateStudent(studentIndex, students.get(studentIndex));
    }

    private void reloadAll() {
        students = detailRepo.getStudentsForWeek(currentWeekStart, weekFullDates, weekLessonIds, periodStart, periodEnd);
        studentsAdapter.updateData(students);
    }

    private void reloadAndAnimate(int studentIndex, int dateIndex, String value) {
        students = detailRepo.getStudentsForWeek(currentWeekStart, weekFullDates, weekLessonIds, periodStart, periodEnd);
        JournalStudentItem updated = students.get(studentIndex);
        studentsAdapter.updateStudent(studentIndex, updated);
        setStatusSaved();
    }


    private static class ScrollSyncHelper {
        private boolean isSyncing = false;

        void sync(HorizontalScrollView source, HorizontalScrollView target, int scrollX) {
            if (isSyncing) return;
            isSyncing = true;
            target.scrollTo(scrollX, 0);
            isSyncing = false;
        }
    }

    private final ScrollSyncHelper scrollSyncHelper = new ScrollSyncHelper();


    private class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> {
        private List<JournalStudentItem> data;

        StudentsAdapter(List<JournalStudentItem> data) {
            this.data = data;
        }

        void updateData(List<JournalStudentItem> newData) {
            data = newData;
            notifyDataSetChanged();
        }

        void updateStudent(int index, JournalStudentItem item) {
            if (index >= 0 && index < data.size()) {
                data.set(index, item);
                notifyItemChanged(index);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_teacher_journal_student, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            JournalStudentItem item = data.get(position);
            holder.tvStudentName.setText(item.getStudentName());
            holder.tvAverageMark.setText(item.getAverageMark());
            holder.studentIndex = position;

            ColorScheme colorScheme = new ColorScheme(requireContext());
            holder.llMarks.removeAllViews();

            for (int i = 0; i < item.getMarks().length; i++) {
                int dateIndex = i;
                View markView = LayoutInflater.from(holder.itemView.getContext())
                        .inflate(R.layout.item_teacher_journal_mark, holder.llMarks, false);
                TextView tvMark = markView.findViewById(R.id.tv_mark);

                String value = item.getMarks()[dateIndex];

                if (value == null || value.isEmpty() || value.equals("+")) {
                    tvMark.setText("+");
                    tvMark.setTextColor(ContextCompat.getColor(requireContext(), R.color.grade_cell_empty_text));
                    tvMark.setBackgroundResource(R.drawable.bg_grade_chip_empty);
                    tvMark.setBackgroundTintList(null);
                } else {
                    tvMark.setText(value);
                    int bgColor = colorScheme.getGradeBgColor(value);
                    int textColor = colorScheme.getGradeTextColor(value);
                    tvMark.setBackgroundResource(R.drawable.bg_grade_chip_filled);
                    tvMark.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),
                            colorScheme.getGradeBgColorRes(value)));
                    tvMark.setTextColor(textColor);
                }

                tvMark.setOnClickListener(v -> onGradeCellClick(position, dateIndex));
                tvMark.setOnLongClickListener(v -> {
                    onGradeCellLongClick(position, dateIndex);
                    return true;
                });

                holder.llMarks.addView(markView);
            }

            
            holder.hsvMarks.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (hsvDatesHeader != null) {
                    scrollSyncHelper.sync(holder.hsvMarks, hsvDatesHeader, scrollX);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvStudentName, tvAverageMark;
            HorizontalScrollView hsvMarks;
            LinearLayout llMarks;
            int studentIndex;

            ViewHolder(View v) {
                super(v);
                tvStudentName = v.findViewById(R.id.tv_student_name);
                tvAverageMark = v.findViewById(R.id.tv_average_mark);
                hsvMarks = v.findViewById(R.id.hsv_marks);
                llMarks = v.findViewById(R.id.ll_marks_container);
            }
        }
    }


    private static class ColorScheme {
        private final Context context;

        ColorScheme(Context context) {
            this.context = context;
        }

        int getGradeBgColorRes(String value) {
            if (value != null && value.contains("\u00B7")) {
                return R.color.marks_purple_light;
            }
            switch (value) {
                case "5": return R.color.grade_bg_5;
                case "4": return R.color.grade_bg_4;
                case "3": return R.color.grade_bg_3;
                case "2": return R.color.grade_bg_2;
                case "Н": return R.color.grade_bg_n;
                case "Б": return R.color.grade_bg_b;
                case "Зач": return R.color.grade_bg_5;
                default: return R.color.marks_purple_light;
            }
        }

        int getGradeBgColor(String value) {
            return ContextCompat.getColor(context, getGradeBgColorRes(value));
        }

        int getGradeTextColor(String value) {
            if (value != null && value.contains("\u00B7")) {
                return ContextCompat.getColor(context, R.color.marks_purple_main);
            }
            switch (value) {
                case "5": return ContextCompat.getColor(context, R.color.grade_text_5);
                case "4": return ContextCompat.getColor(context, R.color.grade_text_4);
                case "3": return ContextCompat.getColor(context, R.color.grade_text_3);
                case "2": return ContextCompat.getColor(context, R.color.grade_text_2);
                case "Н": return ContextCompat.getColor(context, R.color.grade_text_n);
                case "Б": return ContextCompat.getColor(context, R.color.grade_text_b);
                case "Зач": return ContextCompat.getColor(context, R.color.grade_text_5);
                default: return ContextCompat.getColor(context, R.color.marks_purple_main);
            }
        }
    }
}
