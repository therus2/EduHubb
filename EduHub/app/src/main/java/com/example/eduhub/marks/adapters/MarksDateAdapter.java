package com.example.eduhub.marks.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.marks.models.MarkDateItem;

import java.util.List;


public class MarksDateAdapter extends RecyclerView.Adapter<MarksDateAdapter.MarkViewHolder> {

    private List<MarkDateItem> items;

    public MarksDateAdapter(List<MarkDateItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mark_date, parent, false);
        return new MarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkViewHolder holder, int position) {
        MarkDateItem item = items.get(position);

        
        if (item.getDateHeader() != null && !item.getDateHeader().isEmpty()) {
            holder.textDateHeader.setVisibility(View.VISIBLE);
            holder.textDateHeader.setText(item.getDateHeader());
        } else {
            holder.textDateHeader.setVisibility(View.GONE);
        }

        holder.textSubject.setText(item.getSubjectName());
        holder.textDescription.setText(item.getDescription());
        holder.textGrade.setText(String.valueOf(item.getGrade()));
        holder.textTeacher.setText("Преподаватель: " + item.getTeacher());
        holder.textComment.setText(item.getComment());

        
        if (item.getGrade() <= 3) {
            holder.textGrade.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.marks_text_red));
            holder.boxGrade.setBackgroundResource(R.drawable.bg_marks_grade_box_red);
            holder.textDescription.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.marks_text_red));
        } else {
            holder.textGrade.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.marks_purple_main));
            holder.boxGrade.setBackgroundResource(R.drawable.bg_marks_grade_box_purple);
            holder.textDescription.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.marks_text_gray));
        }

        
        boolean expanded = item.isExpanded();
        holder.expandedContent.setVisibility(expanded ? View.VISIBLE : View.GONE);
        holder.iconArrow.setRotation(expanded ? 90 : 270);

        holder.itemView.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MarkViewHolder extends RecyclerView.ViewHolder {
        TextView textDateHeader;
        TextView textSubject;
        TextView textDescription;
        TextView textGrade;
        TextView textTeacher;
        TextView textComment;
        View boxGrade;
        View expandedContent;
        ImageView iconArrow;

        public MarkViewHolder(@NonNull View itemView) {
            super(itemView);
            textDateHeader = itemView.findViewById(R.id.text_date_header);
            textSubject = itemView.findViewById(R.id.text_subject_name);
            textDescription = itemView.findViewById(R.id.text_description);
            textGrade = itemView.findViewById(R.id.text_grade);
            textTeacher = itemView.findViewById(R.id.text_grade_teacher);
            textComment = itemView.findViewById(R.id.text_grade_comment);
            boxGrade = itemView.findViewById(R.id.box_grade);
            expandedContent = itemView.findViewById(R.id.expanded_grade_content);
            iconArrow = itemView.findViewById(R.id.icon_grade_arrow);
        }
    }
}
