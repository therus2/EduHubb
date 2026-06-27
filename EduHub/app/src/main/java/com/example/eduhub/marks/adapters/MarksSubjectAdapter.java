package com.example.eduhub.marks.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.marks.models.MarkSubjectItem;

import java.util.List;
import java.util.Locale;


public class MarksSubjectAdapter extends RecyclerView.Adapter<MarksSubjectAdapter.SubjectViewHolder> {

    private List<MarkSubjectItem> items;
    private String studentId;

    public MarksSubjectAdapter(List<MarkSubjectItem> items, String studentId) {
        this.items = items;
        this.studentId = studentId;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mark_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        MarkSubjectItem item = items.get(position);

        holder.textSubject.setText(item.getSubjectName());
        holder.textStatus.setText(item.getStatusText());

        if (item.getGradeCount() == 0) {
            
            holder.textAverage.setText("—");
            holder.progressBar.setVisibility(View.GONE);
            holder.textAverage.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.marks_text_gray));
            holder.boxAverage.setBackgroundResource(R.drawable.bg_marks_pill_gray);
            holder.textStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.marks_text_gray));
        } else {
            holder.textAverage.setText(String.format(Locale.US, "%.2f", item.getAverage()));
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBar.setProgress(item.getProgress());

            if (item.getAverage() < 4.0) {
                holder.textAverage.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.marks_text_red));
                holder.boxAverage.setBackgroundResource(R.drawable.bg_marks_pill_red);
                holder.textStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.marks_text_red));
            } else if (item.getAverage() < 4.5) {
                holder.textAverage.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.marks_purple_main));
                holder.boxAverage.setBackgroundResource(R.drawable.bg_marks_pill_purple);
                holder.textStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.marks_text_gray));
            } else {
                holder.textAverage.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.marks_purple_main));
                holder.boxAverage.setBackgroundResource(R.drawable.bg_marks_pill_purple);
                holder.textStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.marks_text_green));
            }
        }

        holder.itemView.setOnClickListener(v -> {
            com.example.eduhub.marks.subject.SubjectDetailsFragment fragment = new com.example.eduhub.marks.subject.SubjectDetailsFragment();
            android.os.Bundle args = new android.os.Bundle();
            args.putString("studentId", studentId);
            args.putString("subjectId", item.getSubjectId());
            args.putString("subjectName", item.getSubjectName());
            args.putString("teacherName", item.getTeacherName());
            args.putFloat("average", (float) item.getAverage());
            args.putInt("gradeCount", item.getGradeCount());
            fragment.setArguments(args);

            androidx.navigation.Navigation.findNavController(v)
                    .navigate(R.id.subjectDetailsFragment, args);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    
    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView textSubject;
        TextView textAverage;
        ProgressBar progressBar;
        TextView textStatus;
        View boxAverage;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            textSubject = itemView.findViewById(R.id.text_subject_name);
            textAverage = itemView.findViewById(R.id.text_average);
            progressBar = itemView.findViewById(R.id.progress_bar);
            textStatus = itemView.findViewById(R.id.text_status);
            boxAverage = itemView.findViewById(R.id.box_average);
        }
    }
}
