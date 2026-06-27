package com.example.eduhub.notifications;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.notifications.models.NotificationItem;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<NotificationItem> items;
    private OnMarkReadListener listener;

    public interface OnMarkReadListener {
        void onMarkRead(String notificationId, int position);
    }

    public NotificationsAdapter(List<NotificationItem> items, OnMarkReadListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = items.get(position);

        holder.textTitle.setText(item.getTitle());
        holder.textSubtitle.setText(item.getSubtitle());
        holder.textDetails.setText(item.getDetails());
        holder.textTime.setText(item.getTime());
        holder.textIcon.setText(item.getIcon());

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(Color.parseColor("#F4ECFF"));
        holder.textIcon.setBackground(shape);
        holder.textIcon.setTextColor(Color.parseColor("#8B29EE"));

        boolean read = item.isRead();
        holder.indicatorLine.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                read ? R.color.marks_text_gray : R.color.marks_purple_main));
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                read ? R.color.marks_gray_light : android.R.color.white));

        holder.expandedContent.setVisibility(item.isExpanded() ? View.VISIBLE : View.GONE);
        holder.iconArrow.setRotation(item.isExpanded() ? 90 : 270);

        holder.btnMarkRead.setVisibility(!read && item.isExpanded() ? View.VISIBLE : View.GONE);

        holder.btnMarkRead.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMarkRead(item.getId(), position);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            boolean expanded = item.isExpanded();
            item.setExpanded(!expanded);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        View indicatorLine;
        TextView textIcon, textTitle, textSubtitle, textDetails, textTime, btnMarkRead;
        View expandedContent;
        ImageView iconArrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            indicatorLine = itemView.findViewById(R.id.indicator_line);
            textIcon = itemView.findViewById(R.id.text_notify_icon);
            textTitle = itemView.findViewById(R.id.text_notify_title);
            textSubtitle = itemView.findViewById(R.id.text_notify_subtitle);
            textDetails = itemView.findViewById(R.id.text_notify_details);
            textTime = itemView.findViewById(R.id.text_notify_time);
            btnMarkRead = itemView.findViewById(R.id.btn_mark_read);
            expandedContent = itemView.findViewById(R.id.expanded_notify_content);
            iconArrow = itemView.findViewById(R.id.icon_notify_arrow);
        }
    }
}