package com.example.eduhub.schedule.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduhub.R;
import com.example.eduhub.schedule.models.DataSchedule;

import java.time.LocalDate;
import java.util.List;


public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ProductViewHolder>{

    public interface OnDaySelectedListener {
        void onDaySelected(LocalDate date);
    }

    private Context context;
    private List<DataSchedule> days;
    private LayoutInflater inflater;
    private int selectedPosition = -1;
    private OnDaySelectedListener listener;

    public DataAdapter(Context context, List<DataSchedule> days) {
        this.context = context;
        this.days = days;
        inflater = LayoutInflater.from(context);
    }

    public void setOnDaySelectedListener(OnDaySelectedListener listener) {
        this.listener = listener;
    }

    public void setSelectedDay(LocalDate date) {
        if (date == null) return;
        for (int i = 0; i < days.size(); i++) {
            DataSchedule d = days.get(i);
            if (date.equals(d.getDate())) {
                selectedPosition = i;
                notifyDataSetChanged();
                return;
            }
        }
    }

    public int getSelectedWeekPosition() {
        if (selectedPosition < 0) return 0;
        return selectedPosition / 7;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(
                inflater.inflate(R.layout.item_schedule_week, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        for (int i = 0; i < 7; i++) {
            int dayIndex = position * 7 + i;
            View dayView = holder.daysViews[i];

            if (dayIndex < days.size()) {
                dayView.setVisibility(View.VISIBLE);
                DataSchedule dayy = days.get(dayIndex);
                TextView day_of_week = dayView.findViewById(R.id.day_of_week);
                TextView data_of_month = dayView.findViewById(R.id.data_of_month);
                View container_date = dayView.findViewById(R.id.container_date);

                day_of_week.setText(dayy.getDay_of_week());
                data_of_month.setText(String.valueOf(dayy.getNumber_of_month()));

                boolean isSelected = dayIndex == selectedPosition;
                if (isSelected) {
                    container_date.setBackgroundResource(R.drawable.bg_schedule_calendar_selected);
                    data_of_month.setTextColor(context.getResources().getColor(R.color.white));
                } else {
                    container_date.setBackground(null);
                    data_of_month.setTextColor(context.getResources().getColor(
                            dayy.isCurrentMonth() ? R.color.black : R.color.calendar_text_gray));
                }

                LocalDate clickDate = dayy.getDate();
                dayView.setOnClickListener(v -> {
                    selectedPosition = dayIndex;
                    notifyDataSetChanged();
                    if (listener != null) {
                        listener.onDaySelected(clickDate);
                    }
                });
            } else {
                dayView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (int) Math.ceil(days.size() / 7.0);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{
        private View[] daysViews;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            daysViews = new View[]{
                    itemView.findViewById(R.id.day1),
                    itemView.findViewById(R.id.day2),
                    itemView.findViewById(R.id.day3),
                    itemView.findViewById(R.id.day4),
                    itemView.findViewById(R.id.day5),
                    itemView.findViewById(R.id.day6),
                    itemView.findViewById(R.id.day7)
            };
        }
    }
}