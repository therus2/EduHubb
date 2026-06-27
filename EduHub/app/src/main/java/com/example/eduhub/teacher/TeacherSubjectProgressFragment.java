package com.example.eduhub.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.teacher.models.LessonTopicItem;
import com.example.eduhub.teacher.repository.TeacherSubjectProgressRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeacherSubjectProgressFragment extends Fragment {

    private String subjectId;
    private String subjectName;
    private TeacherSubjectProgressRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_subject_progress, container, false);

        Bundle args = getArguments();
        if (args != null) {
            subjectId = args.getString("subjectId");
            subjectName = args.getString("subjectName", "");
        }

        repository = new TeacherSubjectProgressRepository(requireContext(), subjectId);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(subjectName);

        RecyclerView rvTopics = view.findViewById(R.id.rv_topics);
        rvTopics.setLayoutManager(new LinearLayoutManager(getContext()));

        Map<String, List<LessonTopicItem>> grouped = repository.getLessonsByGroup();
        List<Object> flatList = new ArrayList<>();

        for (Map.Entry<String, List<LessonTopicItem>> entry : grouped.entrySet()) {
            flatList.add(new SectionHeader(entry.getKey()));
            flatList.addAll(entry.getValue());
        }

        rvTopics.setAdapter(new TopicAdapter(flatList));

        View btnBack = view.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        return view;
    }

    private static String translateLessonType(String type) {
        if (type == null) return "";
        switch (type) {
            case "LECTURE":       return "Лекция";
            case "PRACTICAL":     return "Практика";
            case "LABORATORY":    return "Лабораторная";
            case "SEMINAR":       return "Семинар";
            case "CONSULTATION":  return "Консультация";
            case "EXAM":          return "Экзамен";
            case "CREDIT":        return "Зачёт";
            default:              return type;
        }
    }

    private static class SectionHeader {
        final String title;
        SectionHeader(String title) { this.title = title; }
    }

    private static class TopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        private final List<Object> data;

        TopicAdapter(List<Object> data) { this.data = data; }

        @Override
        public int getItemViewType(int position) {
            return data.get(position) instanceof SectionHeader ? TYPE_HEADER : TYPE_ITEM;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                TextView tv = new TextView(parent.getContext());
                tv.setPadding(0, 16, 0, 8);
                tv.setTextSize(18);
                tv.setTypeface(null, android.graphics.Typeface.BOLD);
                tv.setTextColor(0xFF000000);
                return new RecyclerView.ViewHolder(tv) {};
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_lesson_topic, parent, false);
                return new RecyclerView.ViewHolder(v) {};
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == TYPE_HEADER) {
                SectionHeader header = (SectionHeader) data.get(position);
                ((TextView) holder.itemView).setText(header.title);
            } else {
                LessonTopicItem item = (LessonTopicItem) data.get(position);
                View v = holder.itemView;
                ((TextView) v.findViewById(R.id.tv_topic_name)).setText(item.getTopic());
                ((TextView) v.findViewById(R.id.tv_day_letter)).setText(item.getDayName());
                ((TextView) v.findViewById(R.id.tv_lesson_info)).setText(
                        item.getDayName() + " " + item.getStartTime() + "-" + item.getEndTime()
                                + " • " + item.getClassroom() + " • " + translateLessonType(item.getLessonType()));
                ((TextView) v.findViewById(R.id.tv_group_badge)).setText(item.getGroupCode());
            }
        }

        @Override
        public int getItemCount() { return data.size(); }
    }
}
