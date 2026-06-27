package com.example.eduhub.domains.classes;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class StudentTest {

    @Test
    public void constructor_ShouldInitializeDefaults() {
        Student student = new Student();

        assertNotNull(student.getId());
        assertTrue(student.isActive());
        assertNotNull(student.getReadAnnouncementIds());
        assertTrue(student.getReadAnnouncementIds().isEmpty());
        assertNotNull(student.getNotificationSettings());
        assertEquals(4, student.getNotificationSettings().size());
    }

    @Test
    public void getRole_ShouldReturnStudent() {
        Student student = new Student();

        assertEquals(User.UserRole.STUDENT, student.getRole());
    }

    @Test
    public void getFullName_ShouldReturnFormattedName() {
        Student student = new Student();
        student.setLastName("Ivanov");
        student.setFirstName("Ivan");
        student.setPatronymic("Petrovich");

        String fullName = student.getFullName();

        assertEquals("Ivanov Ivan Petrovich", fullName);
    }

    @Test
    public void getFullName_ShouldHandleNoPatronymic() {
        Student student = new Student();
        student.setLastName("Sidorov");
        student.setFirstName("Sidor");

        String fullName = student.getFullName();

        assertEquals("Sidorov Sidor", fullName);
    }

    @Test
    public void canViewGrades_ShouldReturnTrue_WhenActiveAndHasGroup() {
        Student student = new Student();
        student.setGroup(new StudentGroup());

        assertTrue(student.canViewGrades());
    }

    @Test
    public void canViewGrades_ShouldReturnFalse_WhenNoGroup() {
        Student student = new Student();

        assertFalse(student.canViewGrades());
    }

    @Test
    public void canViewGrades_ShouldReturnFalse_WhenInactive() {
        Student student = new Student();
        student.setGroup(new StudentGroup());
        student.setActive(false);

        assertFalse(student.canViewGrades());
    }

    @Test
    public void markAnnouncementAsRead_ShouldAddId() {
        Student student = new Student();
        UUID announcementId = UUID.randomUUID();

        student.markAnnouncementAsRead(announcementId);

        assertTrue(student.isAnnouncementRead(announcementId));
    }

    @Test
    public void isAnnouncementRead_ShouldReturnFalse_WhenNotRead() {
        Student student = new Student();

        assertFalse(student.isAnnouncementRead(UUID.randomUUID()));
    }

    @Test
    public void toggleNotificationSetting_ShouldUpdateSetting() {
        Student student = new Student();

        student.toggleNotificationSetting("newGrades", false);

        assertFalse(student.isNotificationEnabled("newGrades"));
    }

    @Test
    public void toggleNotificationSetting_ShouldNotUpdateInvalidKey() {
        Student student = new Student();

        student.toggleNotificationSetting("invalidKey", false);

        assertTrue(student.isNotificationEnabled("newGrades"));
    }

    @Test
    public void shouldReceiveNotification_ShouldCheckCorrectSetting() {
        Student student = new Student();

        student.toggleNotificationSetting("announcements", false);

        assertFalse(student.shouldReceiveNotification(Student.NotificationType.ANNOUNCEMENT));
    }

    @Test
    public void deactivate_ShouldSetInactive() {
        Student student = new Student();

        student.deactivate();

        assertFalse(student.isActive());
    }

    @Test
    public void activate_ShouldSetActive() {
        Student student = new Student();
        student.deactivate();

        student.activate();

        assertTrue(student.isActive());
    }
}
