package com.example.eduhub.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.teacher.repository.TeacherLessonDetailRepository;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherAttendanceFragment extends Fragment {

    private static final String ARG_LESSON_ID = "lessonId";
    private static final String ARG_DATE = "date";
    private static final String ARG_GROUP_ID = "groupId";

    private String lessonId, date, groupId, teacherId;
    private TeacherLessonDetailRepository repository;
    private List<String[]> students;
    private Map<String, String> statusMap;
    private AttendanceAdapter adapter;

    public static TeacherAttendanceFragment newInstance(String lessonId, String date, String groupId) {
        TeacherAttendanceFragment f = new TeacherAttendanceFragment();
        Bundle b = new Bundle();
        b.putString(ARG_LESSON_ID, lessonId);
        b.putString(ARG_DATE, date);
        b.putString(ARG_GROUP_ID, groupId);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lessonId = getArguments().getString(ARG_LESSON_ID);
            date = getArguments().getString(ARG_DATE);
            groupId = getArguments().getString(ARG_GROUP_ID);
            teacherId = getArguments().getString("teacherId");
        }
        if (teacherId == null || teacherId.isEmpty()) {
            teacherId = UserSessionManager.getInstance(requireContext()).getTeacherId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_attendance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new TeacherLessonDetailRepository(requireContext());

        view.findViewById(R.id.btn_back).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        TextView tvDate = view.findViewById(R.id.tv_date);
        tvDate.setText(date != null ? date : "—");

        students = repository.getStudents(groupId);
        statusMap = repository.getAttendanceMap(lessonId, date);

        RecyclerView rv = view.findViewById(R.id.rv_students);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AttendanceAdapter(students, statusMap);
        rv.setAdapter(adapter);

        MaterialButton btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> {
            repository.saveAttendance(lessonId, date, statusMap, teacherId);
            getParentFragmentManager().popBackStack();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (repository != null) repository.close();
    }

    private class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.VH> {
        private final List<String[]> students;

        AttendanceAdapter(List<String[]> students, Map<String, String> statusMap) {
            this.students = students;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_attendance_student, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            String[] s = students.get(position);
            String sid = s[0];
            holder.tvName.setText(s[1]);

            holder.rg.setOnCheckedChangeListener(null);
            String st = statusMap.get(sid);
            if ("PRESENT".equals(st)) holder.rg.check(R.id.rb_present);
            else if ("ABSENT".equals(st)) holder.rg.check(R.id.rb_absent);
            else if ("LATE".equals(st)) holder.rg.check(R.id.rb_late);
            else if ("EXCUSED_ABSENT".equals(st)) holder.rg.check(R.id.rb_excused);
            else holder.rg.clearCheck();

            holder.rg.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rb_present) statusMap.put(sid, "PRESENT");
                else if (checkedId == R.id.rb_absent) statusMap.put(sid, "ABSENT");
                else if (checkedId == R.id.rb_late) statusMap.put(sid, "LATE");
                else if (checkedId == R.id.rb_excused) statusMap.put(sid, "EXCUSED_ABSENT");
                else statusMap.remove(sid);
            });
        }

        @Override
        public int getItemCount() { return students.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tvName;
            RadioGroup rg;
            VH(View v) {
                super(v);
                tvName = v.findViewById(R.id.tv_student_name);
                rg = v.findViewById(R.id.rg_attendance);
            }
        }
    }
}
