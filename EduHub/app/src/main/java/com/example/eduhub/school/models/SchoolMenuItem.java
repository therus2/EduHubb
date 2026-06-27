package com.example.eduhub.school.models;

public class SchoolMenuItem {
    private String title;
    private String subtitle;

    public SchoolMenuItem(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
