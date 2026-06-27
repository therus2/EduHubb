package com.example.eduhub.domains.classes;

import java.time.LocalDate;
import java.util.*;

public class Student extends User {
    private StudentGroup group;
    private LocalDate enrollmentDate;
    private String studentIdNumber;
    private Map<String, Boolean> notificationSettings;
    private Set<UUID> readAnnouncementIds;

    public StudentGroup getGroup() { return group; }
    public void setGroup(StudentGroup group) { this.group = group; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public String getStudentIdNumber() { return studentIdNumber; }
    public void setStudentIdNumber(String studentIdNumber) { this.studentIdNumber = studentIdNumber; }

    public Map<String, Boolean> getNotificationSettings() { return notificationSettings; }
    public void setNotificationSettings(Map<String, Boolean> notificationSettings) { this.notificationSettings = notificationSettings; }

    public Set<UUID> getReadAnnouncementIds() { return readAnnouncementIds; }
    public void setReadAnnouncementIds(Set<UUID> readAnnouncementIds) { this.readAnnouncementIds = readAnnouncementIds; }

    public Student() {
        super();
        this.notificationSettings = new HashMap<>();
        this.readAnnouncementIds = new HashSet<>();
        initializeNotificationSettings();
    }

    private void initializeNotificationSettings() {
        notificationSettings.put("newGrades", true);
        notificationSettings.put("scheduleChanges", true);
        notificationSettings.put("homework", true);
        notificationSettings.put("announcements", true);
    }

    @Override
    public UserRole getRole() {
        return UserRole.STUDENT;
    }

    public boolean canViewGrades() {
        return isActive && group != null;
    }

    public boolean canViewSchedule() {
        return isActive && group != null;
    }

    public void markAnnouncementAsRead(UUID announcementId) {
        readAnnouncementIds.add(announcementId);
    }

    public boolean isAnnouncementRead(UUID announcementId) {
        return readAnnouncementIds.contains(announcementId);
    }

    public void toggleNotificationSetting(String settingKey, boolean enabled) {
        if (notificationSettings.containsKey(settingKey)) {
            notificationSettings.put(settingKey, enabled);
        }
    }

    public boolean isNotificationEnabled(String settingKey) {
        return notificationSettings.getOrDefault(settingKey, false);
    }

    public boolean shouldReceiveNotification(NotificationType type) {
        switch (type) {
            case NEW_GRADE:
                return isNotificationEnabled("newGrades");
            case SCHEDULE_CHANGE:
                return isNotificationEnabled("scheduleChanges");
            case HOMEWORK:
                return isNotificationEnabled("homework");
            case ANNOUNCEMENT:
                return isNotificationEnabled("announcements");
            default:
                return false;
        }
    }

    public enum NotificationType {
        NEW_GRADE,
        SCHEDULE_CHANGE,
        HOMEWORK,
        ANNOUNCEMENT
    }
}
