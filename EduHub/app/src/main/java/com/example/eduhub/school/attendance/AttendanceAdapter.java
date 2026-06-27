package com.example.eduhub.school.attendance;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.school.attendance.models.AttendanceItem;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    private List<AttendanceItem> items;

    public AttendanceAdapter(List<AttendanceItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceItem item = items.get(position);

        holder.textClass.setText(item.getClassName());
        holder.textTimeStatus.setText(item.getTime() + " • " + item.getStatusText());
        holder.textDetails.setText(item.getDetails());

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);

        if (item.getStatusType() == 0) { 
            holder.textTimeStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.textIcon.setText("✓");
            holder.textIcon.setTextColor(Color.parseColor("#4CAF50"));
            shape.setColor(Color.parseColor("#E8F5E9"));
            shape.setStroke(2, Color.parseColor("#4CAF50"));
        } else if (item.getStatusType() == 1) { 
            holder.textTimeStatus.setTextColor(Color.parseColor("#FFA000"));
            holder.textIcon.setText("🕒");
            holder.textIcon.setTextColor(Color.parseColor("#FFA000"));
            shape.setColor(Color.parseColor("#FFF8E1"));
            shape.setStroke(2, Color.parseColor("#FFA000"));
        } else { 
            holder.textTimeStatus.setTextColor(Color.parseColor("#F44336"));
            holder.textIcon.setText("×");
            holder.textIcon.setTextColor(Color.parseColor("#F44336"));
            shape.setColor(Color.parseColor("#FFEBEE"));
            shape.setStroke(2, Color.parseColor("#F44336"));
        }
        holder.textIcon.setBackground(shape);

        boolean expandable = item.hasExpandableContent();
        if (!expandable) {
            item.setExpanded(false);
        }
        holder.expandedContent.setVisibility(expandable && item.isExpanded() ? View.VISIBLE : View.GONE);
        holder.textDetails.setVisibility(expandable ? View.VISIBLE : View.GONE);

        if (expandable) {
            holder.itemView.setClickable(true);
            holder.itemView.setOnClickListener(v -> {
                item.setExpanded(!item.isExpanded());
                notifyItemChanged(holder.getBindingAdapterPosition());
            });
        } else {
            holder.itemView.setOnClickListener(null);
            holder.itemView.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textClass, textTimeStatus, textIcon, textDetails;
        View expandedContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textClass = itemView.findViewById(R.id.text_class_name);
            textTimeStatus = itemView.findViewById(R.id.text_class_time_status);
            textIcon = itemView.findViewById(R.id.text_attendance_icon);
            textDetails = itemView.findViewById(R.id.text_attendance_details);
            expandedContent = itemView.findViewById(R.id.expanded_attendance_content);
        }
    }
}
