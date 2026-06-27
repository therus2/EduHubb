package com.example.eduhub.tasks.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.tasks.models.TaskItem;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    private List<TaskItem> items;
    private OnTaskActionListener listener;

    public interface OnTaskActionListener {
        void onSubmitTask(String assignmentId, String comment);
    }

    public TasksAdapter(List<TaskItem> items) {
        this.items = items;
    }

    public void setOnTaskActionListener(OnTaskActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskItem item = items.get(position);

        holder.textSubject.setText(item.getSubjectName());
        holder.textDescription.setText(item.getDescription());
        holder.textStatus.setText(item.getStatusText());
        holder.textDeadline.setText(item.getDeadlineText());

        if (item.getStatusType() == TaskItem.STATUS_COMPLETED) {
            holder.textStatus.setBackgroundResource(R.drawable.bg_tasks_status_completed);
            holder.textStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.tasks_status_completed_text));
            holder.textDeadline.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.marks_text_gray));
        } else if (item.getStatusType() == TaskItem.STATUS_REVIEW) {
            holder.textStatus.setBackgroundResource(R.drawable.bg_tasks_status_review);
            holder.textStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.tasks_status_review_text));
            holder.textDeadline.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.marks_text_gray));
        } else if (item.getStatusType() == TaskItem.STATUS_OVERDUE) {
            holder.textStatus.setBackgroundResource(R.drawable.bg_tasks_status_overdue);
            holder.textStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.tasks_status_overdue_text));
            holder.textDeadline.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.tasks_red_text));
        } else {
            holder.textStatus.setBackgroundResource(R.drawable.bg_tasks_status_in_progress);
            holder.textStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.tasks_status_in_progress_text));
            holder.textDeadline.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.tasks_red_text));
        }

        boolean canSubmit = (item.getStatusType() == TaskItem.STATUS_ACTIVE
                || item.getStatusType() == TaskItem.STATUS_OVERDUE)
                && !item.getStatusText().equals("НА ПРОВЕРКЕ")
                && !item.getStatusText().equals("ВЫПОЛНЕНО");
        boolean isSubmittedOrCompleted = item.getStatusText().equals("НА ПРОВЕРКЕ")
                || item.getStatusText().equals("ВЫПОЛНЕНО");
        boolean expandable = item.hasExpandableContent();

        if (!expandable) {
            item.setExpanded(false);
        }
        holder.expandedContent.setVisibility(expandable && item.isExpanded() ? View.VISIBLE : View.GONE);

        holder.etSubmitComment.setVisibility(canSubmit ? View.VISIBLE : View.GONE);
        holder.btnSubmitTask.setVisibility(canSubmit ? View.VISIBLE : View.GONE);
        holder.tvSubmittedInfo.setVisibility(isSubmittedOrCompleted ? View.VISIBLE : View.GONE);

        holder.btnSubmitTask.setOnClickListener(v -> {
            String comment = holder.etSubmitComment.getText().toString().trim();
            if (listener != null) {
                listener.onSubmitTask(item.getAssignmentId(), comment);
            }
        });

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

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textSubject;
        TextView textDescription;
        TextView textStatus;
        TextView textDeadline;
        View expandedContent;
        EditText etSubmitComment;
        Button btnSubmitTask;
        TextView tvSubmittedInfo;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textSubject = itemView.findViewById(R.id.text_task_subject);
            textDescription = itemView.findViewById(R.id.text_task_description);
            textStatus = itemView.findViewById(R.id.text_task_status);
            textDeadline = itemView.findViewById(R.id.text_task_deadline);
            expandedContent = itemView.findViewById(R.id.expanded_content);
            etSubmitComment = itemView.findViewById(R.id.et_submit_comment);
            btnSubmitTask = itemView.findViewById(R.id.btn_submit_task);
            tvSubmittedInfo = itemView.findViewById(R.id.tv_submitted_info);
        }
    }
}
