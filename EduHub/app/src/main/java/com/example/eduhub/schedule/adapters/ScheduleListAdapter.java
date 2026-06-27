package com.example.eduhub.schedule.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.schedule.models.BreakItem;
import com.example.eduhub.schedule.models.LessonItem;
import com.example.eduhub.schedule.models.ScheduleItem;

import java.util.List;


public class ScheduleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ScheduleItem> items;
    private OnLessonClickListener onLessonClickListener;

    public interface OnLessonClickListener {
        void onLessonClick(String lessonId, String title, String info);
    }

    public ScheduleListAdapter(List<ScheduleItem> items) {
        this.items = items;
    }

    public void setOnLessonClickListener(OnLessonClickListener listener) {
        this.onLessonClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ScheduleItem.TYPE_LESSON) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_schedule_lesson, parent, false);
            return new LessonViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_schedule_break, parent, false);
            return new BreakViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ScheduleItem item = items.get(position);
        if (getItemViewType(position) == ScheduleItem.TYPE_LESSON) {
            LessonItem lesson = (LessonItem) item;
            LessonViewHolder h = (LessonViewHolder) holder;
            h.info.setText(lesson.getInfo());
            h.cabinet.setText(lesson.getCabinet());
            h.title.setText(lesson.getTitle());
            h.teacherName.setText(lesson.getTeacherName());

            String hw = lesson.getHomework();
            if (hw == null || hw.isEmpty() || "Не задано".equals(hw)) {
                h.homework.setText("Не задано");
            } else {
                h.homework.setText(hw);
            }

            if (onLessonClickListener != null) {
                h.expandedContent.setVisibility(View.GONE);
                h.expandIcon.setVisibility(View.GONE);
                h.itemView.setOnClickListener(v ->
                    onLessonClickListener.onLessonClick(lesson.getLessonId(), lesson.getTitle(), lesson.getInfo())
                );
            } else {
                boolean isExpanded = lesson.isExpanded();
                h.expandedContent.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                float angle = isExpanded ? 180f : 0f;
                h.expandIcon.setRotation(angle);
                h.expandIcon.setVisibility(View.VISIBLE);
                h.itemView.setOnClickListener(v -> {
                    boolean expanded = lesson.isExpanded();
                    lesson.setExpanded(!expanded);
                    notifyItemChanged(position);
                });
            }
        } else {
            BreakItem b = (BreakItem) item;
            BreakViewHolder h = (BreakViewHolder) holder;
            h.text.setText(b.getText());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    
    static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView info, cabinet, title, teacherName, homework;
        ImageView expandIcon;
        View expandedContent;
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            info = itemView.findViewById(R.id.lesson_info);
            cabinet = itemView.findViewById(R.id.lesson_cabinet);
            title = itemView.findViewById(R.id.lesson_title);
            teacherName = itemView.findViewById(R.id.lesson_teacher_name);
            homework = itemView.findViewById(R.id.lesson_homework);
            expandIcon = itemView.findViewById(R.id.lesson_expand_icon);
            expandedContent = itemView.findViewById(R.id.expanded_lesson_content);
        }
    }

    
    static class BreakViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        public BreakViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.break_text);
        }
    }
}
