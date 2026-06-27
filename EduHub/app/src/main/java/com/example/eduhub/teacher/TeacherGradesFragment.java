package com.example.eduhub.teacher;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.teacher.models.GradeEntry;
import com.example.eduhub.teacher.repository.TeacherLessonDetailRepository;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TeacherGradesFragment extends Fragment {

    private static final String ARG_LESSON_ID = "lessonId";
    private static final String ARG_GROUP_ID = "groupId";
    private static final String ARG_SUBJECT_ID = "subjectId";
    private static final String ARG_DATE = "date";

    private static final Map<String, String> RU_TO_EN_TYPE = new LinkedHashMap<>();
    static {
        RU_TO_EN_TYPE.put("Работа на уроке", "CURRENT");
        RU_TO_EN_TYPE.put("Контрольная", "CONTROL");
        RU_TO_EN_TYPE.put("Тест", "TEST");
        RU_TO_EN_TYPE.put("Практика", "PRACTICAL");
        RU_TO_EN_TYPE.put("ДЗ", "HOMEWORK");
    }

    private String lessonId, groupId, subjectId, teacherId, lessonDate;
    private TeacherLessonDetailRepository repository;
    private List<String[]> students;
    private Map<String, List<GradeEntry>> entriesByStudent;

    public static TeacherGradesFragment newInstance(String lessonId, String groupId, String subjectId) {
        return newInstance(lessonId, groupId, subjectId, null);
    }

    public static TeacherGradesFragment newInstance(String lessonId, String groupId, String subjectId, String date) {
        TeacherGradesFragment f = new TeacherGradesFragment();
        Bundle b = new Bundle();
        b.putString(ARG_LESSON_ID, lessonId);
        b.putString(ARG_GROUP_ID, groupId);
        b.putString(ARG_SUBJECT_ID, subjectId);
        b.putString(ARG_DATE, date);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lessonId = getArguments().getString(ARG_LESSON_ID);
            groupId = getArguments().getString(ARG_GROUP_ID);
            subjectId = getArguments().getString(ARG_SUBJECT_ID);
            lessonDate = getArguments().getString(ARG_DATE);
            teacherId = getArguments().getString("teacherId");
        }
        if (teacherId == null || teacherId.isEmpty()) {
            teacherId = UserSessionManager.getInstance(requireContext()).getTeacherId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_grades, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new TeacherLessonDetailRepository(requireContext());

        view.findViewById(R.id.btn_back).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        String[] info = repository.getLessonInfo(lessonId);
        if (info.length >= 1) {
            ((TextView) view.findViewById(R.id.tv_subject)).setText(info[0]);
        }

        students = repository.getStudents(groupId);
        entriesByStudent = repository.getGradeEntries(lessonId, groupId, subjectId, lessonDate);

        RecyclerView rv = view.findViewById(R.id.rv_students);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(new GradesAdapter());

        MaterialButton btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> {
            collectGradeEntries();
            repository.saveGradeEntries(lessonId, subjectId, teacherId, entriesByStudent);
            getParentFragmentManager().popBackStack();
        });
    }

    private void collectGradeEntries() {
        RecyclerView rv = requireView().findViewById(R.id.rv_students);
        for (int i = 0; i < rv.getChildCount(); i++) {
            ViewHolderTag tag = (ViewHolderTag) rv.getChildAt(i).getTag();
            if (tag == null || tag.holder == null) continue;
            int pos = tag.holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) continue;
            String sid = students.get(pos)[0];
            List<GradeEntry> entries = new ArrayList<>();
            for (int j = 0; j < tag.gradesContainer.getChildCount(); j++) {
                View row = tag.gradesContainer.getChildAt(j);
                Spinner spType = row.findViewById(R.id.spinner_type);
                TextView tvDate = row.findViewById(R.id.tv_grade_date);
                TextView btnGrade = row.findViewById(R.id.btn_grade_value);
                String gradeVal = btnGrade.getText().toString().trim();
                if (!gradeVal.isEmpty() && !gradeVal.equals("—")) {
                    String ruType = spType.getSelectedItem().toString();
                    String enType = RU_TO_EN_TYPE.get(ruType);
                    if (enType == null) enType = "CURRENT";
                    entries.add(new GradeEntry(sid, gradeVal,
                            enType,
                            tvDate.getTag() != null ? tvDate.getTag().toString() : LocalDate.now().toString(),
                            ""));
                }
            }
            entriesByStudent.put(sid, entries);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (repository != null) repository.close();
    }

    private class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_grade_student, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            String[] s = students.get(position);
            String sid = s[0];
            holder.tvName.setText(s[1]);

            holder.gradesContainer.removeAllViews();
            List<GradeEntry> existing = entriesByStudent.get(sid);
            if (existing != null) {
                for (GradeEntry g : existing) {
                    addGradeRow(holder.gradesContainer, g, sid);
                }
            }

            holder.btnAdd.setOnClickListener(v -> {
                GradeEntry empty = new GradeEntry(sid, "", "CURRENT", LocalDate.now().toString(), "");
                addGradeRow(holder.gradesContainer, empty, sid);
            });

            ViewHolderTag tag = new ViewHolderTag();
            tag.holder = holder;
            tag.gradesContainer = holder.gradesContainer;
            holder.itemView.setTag(tag);
        }

        @Override
        public int getItemCount() { return students.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tvName, btnAdd;
            LinearLayout gradesContainer;
            VH(View v) {
                super(v);
                tvName = v.findViewById(R.id.tv_student_name);
                gradesContainer = v.findViewById(R.id.grades_container);
                btnAdd = v.findViewById(R.id.btn_add_grade);
            }
        }
    }

    private void addGradeRow(LinearLayout container, GradeEntry entry, String sid) {
        View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_grade_entry, container, false);
        TextView btnGrade = row.findViewById(R.id.btn_grade_value);
        Spinner spType = row.findViewById(R.id.spinner_type);
        TextView tvDate = row.findViewById(R.id.tv_grade_date);
        TextView btnRemove = row.findViewById(R.id.btn_remove_grade);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                RU_TO_EN_TYPE.keySet().toArray(new String[0]));
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        String enType = entry.getGradeType();
        String ruType = null;
        for (Map.Entry<String, String> e : RU_TO_EN_TYPE.entrySet()) {
            if (e.getValue().equals(enType)) {
                ruType = e.getKey();
                break;
            }
        }
        if (ruType == null) ruType = "Работа на уроке";
        for (int i = 0; i < spType.getAdapter().getCount(); i++) {
            if (spType.getAdapter().getItem(i).toString().equals(ruType)) {
                spType.setSelection(i);
                break;
            }
        }

        String gradeVal = entry.getGradeValue();
        if (gradeVal != null && !gradeVal.isEmpty()) {
            btnGrade.setText(gradeVal);
            btnGrade.setTextColor(0xFF333333);
        } else {
            btnGrade.setText("—");
        }
        btnGrade.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), btnGrade);
            for (int g = 2; g <= 5; g++) {
                popup.getMenu().add(0, g, 0, String.valueOf(g));
            }
            popup.setOnMenuItemClickListener(item -> {
                btnGrade.setText(String.valueOf(item.getItemId()));
                btnGrade.setTextColor(0xFF333333);
                return true;
            });
            popup.show();
        });

        String dateStr = entry.getGradeDate();
        if (dateStr == null || dateStr.isEmpty()) dateStr = LocalDate.now().toString();
        tvDate.setText(dateStr.length() >= 10 ? dateStr.substring(5) : dateStr);
        tvDate.setTag(dateStr);

        final String finalDate = dateStr;
        tvDate.setOnClickListener(v -> {
            LocalDate d = LocalDate.parse(finalDate);
            DatePickerDialog dpd = new DatePickerDialog(requireContext(),
                    (view, year, month, day) -> {
                        String nd = String.format("%04d-%02d-%02d", year, month + 1, day);
                        tvDate.setText(String.format("%02d.%02d", day, month + 1));
                        tvDate.setTag(nd);
                    },
                    d.getYear(), d.getMonthValue() - 1, d.getDayOfMonth());
            dpd.show();
        });

        btnRemove.setOnClickListener(v -> container.removeView(row));

        container.addView(row);
    }

    private static class ViewHolderTag {
        RecyclerView.ViewHolder holder;
        LinearLayout gradesContainer;
    }
}
