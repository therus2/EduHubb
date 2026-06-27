package com.example.eduhub.domains.classes;

import java.util.UUID;

public class Subject {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private int totalHours;
    private int credits;
    private boolean isExam;
    private boolean isCredit;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getTotalHours() { return totalHours; }
    public void setTotalHours(int totalHours) { this.totalHours = totalHours; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public boolean isExam() { return isExam; }
    public void setExam(boolean exam) { isExam = exam; }

    public boolean isCredit() { return isCredit; }
    public void setCredit(boolean credit) { isCredit = credit; }

    public Subject() {
        this.id = UUID.randomUUID();
    }

    public Subject(String code, String name) {
        this();
        this.code = code;
        this.name = name;
    }
}