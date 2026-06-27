package com.example.eduhub.teacher;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eduhub.R;
import com.example.eduhub.database.DBHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeacherNewTaskFragment extends Fragment {

    private DBHelper dbHelper;
    private String teacherId;
    private String selectedDeadline;
    private Spinner subjectSpinner;
    private Spinner classSpinner;
    private TextView tvDeadline;
    private List<String> subjectIds;
    private List<String> groupIds;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_new_task, container, false);

        Bundle args = getArguments();
        teacherId = args != null ? args.getString("teacherId") : null;
        if (teacherId == null || teacherId.isEmpty()) {
            teacherId = com.example.eduhub.network.session.UserSessionManager
                    .getInstance(requireContext()).getTeacherId();
        }
        selectedDeadline = LocalDate.now().plusDays(7).toString();
        dbHelper = DBHelper.getInstance(requireContext());

        subjectSpinner = view.findViewById(R.id.spinner_subject);
        classSpinner = view.findViewById(R.id.spinner_class);

        List<String> subjectNames = new ArrayList<>();
        subjectIds = new ArrayList<>();
        if (teacherId != null) {
            Cursor c = dbHelper.findSubjectsForTeacherRaw(teacherId);
            while (c.moveToNext()) {
                subjectIds.add(c.getString(c.getColumnIndexOrThrow("id")));
                subjectNames.add(c.getString(c.getColumnIndexOrThrow("name")));
            }
            c.close();
        }

        List<String> groupNames = new ArrayList<>();
        groupIds = new ArrayList<>();
        if (teacherId != null) {
            Cursor c = dbHelper.findGroupsForTeacherRaw(teacherId);
            while (c.moveToNext()) {
                groupIds.add(c.getString(c.getColumnIndexOrThrow("id")));
                groupNames.add(c.getString(c.getColumnIndexOrThrow("code")));
            }
            c.close();
        }

        if (getContext() != null) {
            ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, subjectNames);
            subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subjectSpinner.setAdapter(subjectAdapter);

            ArrayAdapter<String> classAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, groupNames);
            classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            classSpinner.setAdapter(classAdapter);
        }

        tvDeadline = view.findViewById(R.id.tv_deadline);

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDeadlineFromSelection();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDeadlineFromSelection();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        tvDeadline.setOnClickListener(v -> {
            LocalDate d = LocalDate.parse(selectedDeadline);
            new DatePickerDialog(requireContext(), (picker, year, month, day) -> {
                selectedDeadline = String.format("%04d-%02d-%02d", year, month + 1, day);
                updateDeadlineDisplay(tvDeadline);
            }, d.getYear(), d.getMonthValue() - 1, d.getDayOfMonth()).show();
        });

        View btnSubmit = view.findViewById(R.id.btn_submit);
        TextView etDescription = view.findViewById(R.id.et_description);

        btnSubmit.setOnClickListener(v -> {
            if (subjectIds.isEmpty() || groupIds.isEmpty()) {
                Toast.makeText(getContext(), "Нет доступных предметов или классов", Toast.LENGTH_SHORT).show();
                return;
            }
            int subjPos = subjectSpinner.getSelectedItemPosition();
            int classPos = classSpinner.getSelectedItemPosition();
            String desc = etDescription.getText().toString().trim();

            if (desc.isEmpty()) {
                Toast.makeText(getContext(), "Введите описание задания", Toast.LENGTH_SHORT).show();
                return;
            }

            String selSubjectId = subjectIds.get(subjPos);
            String selGroupId = groupIds.get(classPos);

            String title = desc.length() > 60 ? desc.substring(0, 60) + "..." : desc;
            String assignmentId = dbHelper.insertAssignment(
                    title,
                    desc,
                    selSubjectId,
                    teacherId,
                    selGroupId,
                    LocalDate.now().toString(),
                    selectedDeadline,
                    "HOMEWORK",
                    10
            );

            if (assignmentId != null) {
                dbHelper.createCompletionsForAssignment(assignmentId, selGroupId);
            }

            String subjectName = dbHelper.findSubjectNameById(selSubjectId);
            if (subjectName == null) subjectName = "Предмет";
            String notifTitle = "Новое домашнее задание";
            String notifMessage = subjectName + ": " + desc;
            for (String uid : dbHelper.findStudentUserIdsInGroup(selGroupId)) {
                dbHelper.insertNotification(uid, notifTitle, notifMessage, "NEW_HOMEWORK");
            }

            Toast.makeText(getContext(), "Задание создано", Toast.LENGTH_SHORT).show();
            etDescription.setText("");
        });

        View btnBack = view.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        return view;
    }

    private void updateDeadlineFromSelection() {
        if (subjectIds.isEmpty() || groupIds.isEmpty()) {
            selectedDeadline = LocalDate.now().plusDays(7).toString();
            updateDeadlineDisplay(tvDeadline);
            return;
        }
        int subjPos = subjectSpinner.getSelectedItemPosition();
        int classPos = classSpinner.getSelectedItemPosition();
        if (subjPos < 0 || classPos < 0 || subjPos >= subjectIds.size() || classPos >= groupIds.size()) {
            selectedDeadline = LocalDate.now().plusDays(7).toString();
            updateDeadlineDisplay(tvDeadline);
            return;
        }
        String groupId = groupIds.get(classPos);
        String subjectId = subjectIds.get(subjPos);

        List<Integer> lessonDays = new ArrayList<>();
        Cursor c = dbHelper.findLessonsByGroupSubjectTeacher(groupId, subjectId, teacherId);
        while (c.moveToNext()) {
            lessonDays.add(c.getInt(c.getColumnIndexOrThrow("day_of_week")));
        }
        c.close();

        selectedDeadline = calcNextLessonDate(lessonDays);
        updateDeadlineDisplay(tvDeadline);
    }

    private String calcNextLessonDate(List<Integer> lessonDaysOfWeek) {
        if (lessonDaysOfWeek.isEmpty()) {
            return LocalDate.now().plusDays(7).toString();
        }
        LocalDate today = LocalDate.now();
        int todayDOW = today.getDayOfWeek().getValue();
        int minDiff = 8;
        for (int dow : lessonDaysOfWeek) {
            int diff = dow - todayDOW;
            if (diff <= 0) diff += 7;
            if (diff < minDiff) minDiff = diff;
        }
        return today.plusDays(minDiff).toString();
    }

    private void updateDeadlineDisplay(TextView tv) {
        try {
            LocalDate d = LocalDate.parse(selectedDeadline);
            tv.setText("До " + d.format(java.time.format.DateTimeFormatter.ofPattern("d MMMM", new java.util.Locale("ru"))));
        } catch (Exception e) {
            tv.setText(selectedDeadline);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.close();
    }
}
