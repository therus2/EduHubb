package com.example.eduhub.school.announcements;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.school.announcements.models.AnnouncementItem;

import java.util.List;

public class AnnouncementsAdapter extends RecyclerView.Adapter<AnnouncementsAdapter.ViewHolder> {

    private List<AnnouncementItem> items;
    private OnAcknowledgeListener listener;

    public interface OnAcknowledgeListener {
        void onAcknowledge(String announcementId, int position);
    }

    public AnnouncementsAdapter(List<AnnouncementItem> items, OnAcknowledgeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnnouncementItem item = items.get(position);

        holder.textTag.setText(item.getTag());
        holder.textTitle.setText(item.getTitle());
        holder.textDesc.setText(item.getDescription());
        holder.textFull.setText(item.getFullText());
        holder.textTime.setText(item.getTime());

        boolean acknowledged = item.isAcknowledged();

        if (item.getTag().equals("ПРИКАЗ")) {
            holder.textTag.setBackgroundResource(R.drawable.bg_marks_pill_purple);
            holder.textTag.setTextColor(Color.parseColor("#9C27B0"));
        } else {
            GradientDrawable greyPill = new GradientDrawable();
            greyPill.setShape(GradientDrawable.RECTANGLE);
            greyPill.setCornerRadius(32);
            greyPill.setColor(Color.parseColor("#EBEBEB"));
            holder.textTag.setBackground(greyPill);
            holder.textTag.setTextColor(Color.parseColor("#9E9E9E"));
        }

        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                acknowledged ? R.color.marks_gray_light : android.R.color.white));

        holder.dotUnread.setVisibility(acknowledged ? View.GONE : View.VISIBLE);
        holder.btnAcknowledge.setVisibility(acknowledged ? View.GONE : View.VISIBLE);
        holder.layoutAcknowledgedStatus.setVisibility(acknowledged ? View.VISIBLE : View.GONE);

        holder.expandedContent.setVisibility(item.isExpanded() ? View.VISIBLE : View.GONE);
        holder.iconArrow.setRotation(item.isExpanded() ? 90 : 270);

        holder.itemView.setOnClickListener(v -> {
            boolean expanded = item.isExpanded();
            item.setExpanded(!expanded);
            notifyItemChanged(position);
        });

        holder.btnAcknowledge.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAcknowledge(item.getId(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textTag, textTitle, textDesc, textFull, btnAcknowledge, textTime;
        LinearLayout layoutAcknowledgedStatus, expandedContent;
        View dotUnread;
        ImageView iconArrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            textTag = itemView.findViewById(R.id.text_ann_tag);
            textTitle = itemView.findViewById(R.id.text_ann_title);
            textDesc = itemView.findViewById(R.id.text_ann_desc);
            textFull = itemView.findViewById(R.id.text_ann_full_text);
            btnAcknowledge = itemView.findViewById(R.id.btn_acknowledge);
            layoutAcknowledgedStatus = itemView.findViewById(R.id.layout_acknowledged_status);
            textTime = itemView.findViewById(R.id.text_ann_time);
            dotUnread = itemView.findViewById(R.id.view_unread_dot);
            iconArrow = itemView.findViewById(R.id.icon_ann_arrow);
            expandedContent = itemView.findViewById(R.id.expanded_ann_content);
        }
    }
}