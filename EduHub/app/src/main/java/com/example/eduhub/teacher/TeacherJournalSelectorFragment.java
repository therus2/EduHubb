package com.example.eduhub.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.network.session.UserSessionManager;
import com.example.eduhub.teacher.models.ClassItem;
import com.example.eduhub.teacher.models.SubjectItem;
import com.example.eduhub.teacher.repository.TeacherJournalSelectorRepository;
import com.example.eduhub.ui.SyncRefreshFragment;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

public class TeacherJournalSelectorFragment extends SyncRefreshFragment {

    private String teacherId;
    private ClassesAdapter classesAdapter;
    private SubjectsAdapter subjectsAdapter;
    private TeacherJournalSelectorRepository selectorRepo;
    private int savedClassPos;
    private int savedSubjectPos;
    private RecyclerView rvClasses;
    private RecyclerView rvSubjects;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            savedClassPos = savedInstanceState.getInt("classPos", 0);
            savedSubjectPos = savedInstanceState.getInt("subjectPos", 0);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("classPos", classesAdapter != null ? classesAdapter.selectedPosition : 0);
        outState.putInt("subjectPos", subjectsAdapter != null ? subjectsAdapter.selectedPosition : 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_journal_selector, container, false);

        Bundle args = getArguments();
        teacherId = args != null ? args.getString("teacherId") : null;
        if (teacherId == null || teacherId.isEmpty()) {
            teacherId = UserSessionManager.getInstance(requireContext()).getTeacherId();
        }
        selectorRepo = new TeacherJournalSelectorRepository(requireContext());

        rvClasses = view.findViewById(R.id.rv_classes);
        rvClasses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        rvSubjects = view.findViewById(R.id.rv_subjects);
        rvSubjects.setLayoutManager(new LinearLayoutManager(getContext()));

        reloadLists();

        Button btnOpenJournal = view.findViewById(R.id.btn_open_journal);
        btnOpenJournal.setOnClickListener(v -> openJournal());

        SwipeRefreshLayout swipe = view.findViewById(R.id.swipe_refresh);
        View journalScroll = view.findViewById(R.id.journal_scroll);
        if (swipe != null && journalScroll != null) {
            bindRefreshScroll(swipe, journalScroll);
        }

        return view;
    }

    @Override
    protected void reloadAfterSync() {
        reloadLists();
    }

    private void reloadLists() {
        if (rvClasses == null || rvSubjects == null) return;
        int classPos = classesAdapter != null ? classesAdapter.selectedPosition : savedClassPos;
        int subjectPos = subjectsAdapter != null ? subjectsAdapter.selectedPosition : savedSubjectPos;
        classesAdapter = new ClassesAdapter(selectorRepo.getClasses(teacherId), classPos);
        rvClasses.setAdapter(classesAdapter);
        subjectsAdapter = new SubjectsAdapter(selectorRepo.getSubjects(teacherId), subjectPos);
        rvSubjects.setAdapter(subjectsAdapter);
    }

    private void openJournal() {
        ClassItem selectedClass = classesAdapter.getSelected();
        SubjectItem selectedSubject = subjectsAdapter.getSelected();
        if (selectedClass == null || selectedSubject == null) return;

        Bundle args = new Bundle();
        args.putString("groupId", selectedClass.getId());
        args.putString("groupName", selectedClass.getClassName());
        args.putString("subjectId", selectedSubject.getId());
        args.putString("subjectName", selectedSubject.getSubjectName());
        args.putString("teacherId", teacherId);

        Navigation.findNavController(requireView())
                .navigate(R.id.teacherJournalDetailFragment, args);
    }

    private void openStudentList(int position) {
        ClassItem item = classesAdapter.getItem(position);
        if (item == null || item.getId().isEmpty()) return;

        Bundle args = new Bundle();
        args.putString("groupId", item.getId());
        args.putString("groupName", item.getClassName());
        args.putString("teacherId", teacherId);

        Navigation.findNavController(requireView())
                .navigate(R.id.teacherStudentListFragment, args);
    }

    private void openSubjectProgress(int position) {
        SubjectItem item = subjectsAdapter.getItem(position);
        if (item == null || item.getId().isEmpty()) return;

        Bundle args = new Bundle();
        args.putString("subjectId", item.getId());
        args.putString("subjectName", item.getSubjectName());
        args.putString("teacherId", teacherId);

        Navigation.findNavController(requireView())
                .navigate(R.id.teacherSubjectProgressFragment, args);
    }


    private class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder> {
        private final List<ClassItem> data;
        int selectedPosition;
        private long lastClickTime;
        private int lastClickPos = -1;

        ClassesAdapter(List<ClassItem> data, int selectedPosition) { this.data = data; this.selectedPosition = selectedPosition; }

        ClassItem getSelected() {
            if (data.isEmpty()) return null;
            return data.get(selectedPosition);
        }

        ClassItem getItem(int pos) {
            if (pos < 0 || pos >= data.size()) return null;
            return data.get(pos);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_class_card, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ClassItem item = data.get(position);
            holder.tvClassName.setText(item.getClassName());
            holder.tvCount.setText(item.getStudentsCount());
            boolean selected = position == selectedPosition;
            CardView card = (CardView) holder.itemView;
            card.setCardBackgroundColor(selected
                    ? 0xFF7C3AED
                    : 0xFF9CA3AF);
            holder.itemView.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                long now = System.currentTimeMillis();
                if (pos == lastClickPos && now - lastClickTime < 300) {
                    openStudentList(pos);
                } else {
                    int old = selectedPosition;
                    selectedPosition = pos;
                    savedClassPos = selectedPosition;
                    notifyItemChanged(old);
                    notifyItemChanged(selectedPosition);
                    lastClickTime = now;
                    lastClickPos = pos;
                }
            });
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvClassName, tvCount;
            ViewHolder(View v) {
                super(v);
                tvClassName = v.findViewById(R.id.tv_class_name);
                tvCount     = v.findViewById(R.id.tv_students_count);
            }
        }
    }


    private class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> {
        private final List<SubjectItem> data;
        int selectedPosition;
        private long lastClickTime;
        private int lastClickPos = -1;

        SubjectsAdapter(List<SubjectItem> data, int selectedPosition) { this.data = data; this.selectedPosition = selectedPosition; }

        SubjectItem getSelected() {
            if (data.isEmpty()) return null;
            return data.get(selectedPosition);
        }

        SubjectItem getItem(int pos) {
            if (pos < 0 || pos >= data.size()) return null;
            return data.get(pos);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_subject, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SubjectItem item = data.get(position);
            holder.tvSubjectName.setText(item.getSubjectName());
            holder.tvProgram.setText(item.getProgramType());
            boolean selected = position == selectedPosition;
            CardView card = (CardView) holder.itemView;
            card.setCardBackgroundColor(selected
                    ? 0xFF7C3AED
                    : 0xFFFFFFFF);
            holder.tvSubjectName.setTextColor(selected ? 0xFFFFFFFF : 0xFF000000);
            holder.tvProgram.setTextColor(selected ? 0xFFFFFFFF : 0xFF6B7280);
            holder.itemView.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                long now = System.currentTimeMillis();
                if (pos == lastClickPos && now - lastClickTime < 300) {
                    openSubjectProgress(pos);
                } else {
                    int old = selectedPosition;
                    selectedPosition = pos;
                    savedSubjectPos = selectedPosition;
                    notifyItemChanged(old);
                    notifyItemChanged(selectedPosition);
                    lastClickTime = now;
                    lastClickPos = pos;
                }
            });
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSubjectName, tvProgram;
            ViewHolder(View v) {
                super(v);
                tvSubjectName = v.findViewById(R.id.tv_subject_name);
                tvProgram     = v.findViewById(R.id.tv_program_type);
            }
        }
    }
}
