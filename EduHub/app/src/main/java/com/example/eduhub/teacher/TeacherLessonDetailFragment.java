package com.example.eduhub.teacher;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.eduhub.R;
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.teacher.repository.TeacherLessonDetailRepository;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;

public class TeacherLessonDetailFragment extends Fragment {

    private static final String ARG_LESSON_ID = "lessonId";
    private static final String ARG_DATE = "date";

    private String lessonId, selectedDate, teacherId;
    private TeacherLessonDetailRepository repository;

    private TextView tvDate;
    private String homeworkDate;

    public static TeacherLessonDetailFragment newInstance(String lessonId, String date) {
        TeacherLessonDetailFragment f = new TeacherLessonDetailFragment();
        Bundle b = new Bundle();
        b.putString(ARG_LESSON_ID, lessonId);
        b.putString(ARG_DATE, date);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lessonId = getArguments().getString(ARG_LESSON_ID);
            selectedDate = getArguments().getString(ARG_DATE);
            teacherId = getArguments().getString("teacherId");
        }
        if (teacherId == null || teacherId.isEmpty()) {
            teacherId = UserSessionManager.getInstance(requireContext()).getTeacherId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_lesson_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new TeacherLessonDetailRepository(requireContext());

        view.findViewById(R.id.btn_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        initInfo(view);
        initLessonTopic(view);
        initHomework(view);
        initButtons(view);
    }

    private void initInfo(View view) {
        String[] info = repository.getLessonInfo(lessonId);
        if (info.length >= 5) {
            ((TextView) view.findViewById(R.id.tv_lesson_title)).setText(info[0]);
            ((TextView) view.findViewById(R.id.tv_lesson_subtitle)).setText(info[1] + " \u2022 " + info[2] + " \u2014 " + info[3]);
            ((TextView) view.findViewById(R.id.tv_lesson_time)).setText("Время: " + info[2] + " \u2014 " + info[3]);
            ((TextView) view.findViewById(R.id.tv_lesson_cabinet)).setText("Кабинет: " + (info[4].isEmpty() ? "—" : info[4]));
            ((TextView) view.findViewById(R.id.tv_lesson_group)).setText("Группа: " + info[1]);
        }
    }

    private void initLessonTopic(View view) {
        EditText etTopic = view.findViewById(R.id.et_lesson_topic);
        MaterialButton btnSaveTopic = view.findViewById(R.id.btn_save_lesson_topic);
        String[] info = repository.getLessonInfo(lessonId);
        if (info.length >= 9 && info[8] != null && !info[8].isEmpty()) {
            etTopic.setText(info[8]);
        }
        btnSaveTopic.setOnClickListener(v -> {
            String topic = etTopic.getText().toString().trim();
            repository.saveLessonTopic(lessonId, topic);
            android.widget.Toast.makeText(requireContext(), "Тема сохранена",
                    android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    private void initHomework(View view) {
        String[] info = repository.getLessonInfo(lessonId);
        if (info.length >= 7) {
            String subjectId = info[6];
            String groupId = info[5];
            String hw = repository.getCurrentHomework(subjectId, groupId, selectedDate);
            if (hw != null && !hw.isEmpty()) {
                ((TextView) view.findViewById(R.id.tv_current_homework)).setText(hw);
            }
        }

        homeworkDate = repository.getNextLessonDate(lessonId);
        tvDate = view.findViewById(R.id.tv_homework_date);
        updateDateDisplay();

        tvDate.setOnClickListener(v -> {
            LocalDate d = LocalDate.parse(homeworkDate);
            new DatePickerDialog(requireContext(), (picker, year, month, day) -> {
                homeworkDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                updateDateDisplay();
            }, d.getYear(), d.getMonthValue() - 1, d.getDayOfMonth()).show();
        });

        EditText etHomework = view.findViewById(R.id.et_new_homework);
        MaterialButton btnSaveHw = view.findViewById(R.id.btn_save_homework);
        btnSaveHw.setOnClickListener(v -> {
            String text = etHomework.getText().toString().trim();
            if (text.isEmpty()) return;
            repository.saveHomework(subjectId(), groupId(), teacherId, text, LocalDate.now().toString(), homeworkDate);
            ((TextView) view.findViewById(R.id.tv_current_homework)).setText(text);
            etHomework.setText("");
        });
    }

    private void initButtons(View view) {
        String[] info = repository.getLessonInfo(lessonId);
        String groupId = info.length >= 6 ? info[5] : null;
        String subjectId = info.length >= 7 ? info[6] : null;

        MaterialButton btnAtt = view.findViewById(R.id.btn_attendance);
        String finalGroupId = groupId;
        btnAtt.setOnClickListener(v -> {
            if (finalGroupId == null) return;
            Bundle args = new Bundle();
            args.putString("lessonId", lessonId);
            args.putString("date", selectedDate);
            args.putString("groupId", finalGroupId);
            Navigation.findNavController(v)
                    .navigate(R.id.teacherAttendanceFragment, args);
        });

        MaterialButton btnGrades = view.findViewById(R.id.btn_grades);
        String finalSubjectId = subjectId;
        btnGrades.setOnClickListener(v -> {
            if (finalGroupId == null || finalSubjectId == null) return;
            Bundle args = new Bundle();
            args.putString("lessonId", lessonId);
            args.putString("date", selectedDate);
            args.putString("groupId", finalGroupId);
            args.putString("subjectId", finalSubjectId);
            args.putString("teacherId", teacherId);
            Navigation.findNavController(v)
                    .navigate(R.id.teacherGradesFragment, args);
        });
    }

    private String subjectId() {
        String[] info = repository.getLessonInfo(lessonId);
        return info.length >= 7 ? info[6] : null;
    }

    private String groupId() {
        String[] info = repository.getLessonInfo(lessonId);
        return info.length >= 6 ? info[5] : null;
    }

    private void updateDateDisplay() {
        String display = homeworkDate.length() >= 10 ? homeworkDate.substring(5) : homeworkDate;
        tvDate.setText(display);
        tvDate.setTag(homeworkDate);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (repository != null) repository.close();
    }
}
