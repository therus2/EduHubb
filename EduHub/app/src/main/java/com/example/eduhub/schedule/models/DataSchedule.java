package com.example.eduhub.schedule.models;

import java.time.LocalDate;

public class DataSchedule {

    private String day_of_week;
    private int number_of_month;
    private LocalDate date;
    private boolean currentMonth;

    public DataSchedule(String day_of_week, int number_of_month, LocalDate date, boolean currentMonth) {
        this.day_of_week = day_of_week;
        this.number_of_month = number_of_month;
        this.date = date;
        this.currentMonth = currentMonth;
    }

    public String getDay_of_week() {
        return day_of_week;
    }

    public int getNumber_of_month() {
        return number_of_month;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isCurrentMonth() {
        return currentMonth;
    }
}
