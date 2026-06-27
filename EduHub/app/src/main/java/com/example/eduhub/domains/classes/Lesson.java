package com.example.eduhub.domains.classes;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public class Lesson {
    private UUID id;
    private StudentGroup group;
    private Subject subject;
    private Teacher teacher;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String classroom;
    private LessonType type;
    private int weekNumber; 
    private boolean isAlternatingWeek;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public StudentGroup getGroup() { return group; }
    public void setGroup(StudentGroup group) { this.group = group; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getClassroom() { return classroom; }
    public void setClassroom(String classroom) { this.classroom = classroom; }

    public LessonType getType() { return type; }
    public void setType(LessonType type) { this.type = type; }

    public int getWeekNumber() { return weekNumber; }
    public void setWeekNumber(int weekNumber) { this.weekNumber = weekNumber; }

    public boolean isAlternatingWeek() { return isAlternatingWeek; }
    public void setAlternatingWeek(boolean alternatingWeek) { isAlternatingWeek = alternatingWeek; }

    public Lesson() {
        this.id = UUID.randomUUID();
        this.weekNumber = 1;
    }

    public int getDurationMinutes() {
        if (startTime == null || endTime == null) return 0;
        return (int) java.time.Duration.between(startTime, endTime).toMinutes();
    }

    public boolean isOnDay(DayOfWeek day) {
        return this.dayOfWeek == day;
    }

    public enum LessonType {
        LECTURE,            
        PRACTICAL,          
        LABORATORY,         
        SEMINAR,            
        CONSULTATION,       
        EXAM,               
        CREDIT              
    }
}