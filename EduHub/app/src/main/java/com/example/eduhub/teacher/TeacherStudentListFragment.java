package com.example.eduhub.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.teacher.models.StudentInfoItem;
import com.example.eduhub.teacher.repository.TeacherStudentRepository;

import java.util.List;

public class TeacherStudentListFragment extends Fragment {

    private String groupId;
    private String groupName;
    private String teacherId;
    private TeacherStudentRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_student_list, container, false);

        Bundle args = getArguments();
        if (args != null) {
            groupId = args.getString("groupId");
            groupName = args.getString("groupName", "");
            teacherId = args.getString("teacherId");
        }

        repository = new TeacherStudentRepository(requireContext(), teacherId);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(groupName);

        TextView tvSubtitle = view.findViewById(R.id.tv_subtitle);
        tvSubtitle.setText("Ученики класса");

        RecyclerView rvStudents = view.findViewById(R.id.rv_students);
        rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));

        List<StudentInfoItem> students = repository.getStudentsInGroup(groupId);
        rvStudents.setAdapter(new StudentsAdapter(students));

        View btnBack = view.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        return view;
    }

    private void openStudentDetail(StudentInfoItem student) {
        Bundle args = new Bundle();
        args.putString("studentId", student.getStudentId());
        args.putString("studentName", student.getStudentName());
        args.putString("groupName", student.getGroupCode());
        args.putString("teacherId", teacherId);

        Navigation.findNavController(requireView())
                .navigate(R.id.teacherStudentDetailFragment, args);
    }

    private class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> {
        private final List<StudentInfoItem> data;

        StudentsAdapter(List<StudentInfoItem> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_student, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            StudentInfoItem item = data.get(position);
            String name = item.getStudentName();
            holder.tvAvatar.setText(name.isEmpty() ? "?" : String.valueOf(name.charAt(0)));
            holder.tvStudentName.setText(name);
            holder.tvPhone.setText(item.getPhoneNumber() != null && !item.getPhoneNumber().isEmpty()
                    ? item.getPhoneNumber() : "Нет телефона");
            holder.itemView.setOnClickListener(v -> openStudentDetail(item));
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvAvatar, tvStudentName, tvPhone;
            ViewHolder(View v) {
                super(v);
                tvAvatar = v.findViewById(R.id.tv_avatar);
                tvStudentName = v.findViewById(R.id.tv_student_name);
                tvPhone = v.findViewById(R.id.tv_student_phone);
            }
        }
    }
}
