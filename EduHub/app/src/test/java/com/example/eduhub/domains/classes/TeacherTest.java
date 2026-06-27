package com.example.eduhub.domains.classes;

import org.junit.Test;

import static org.junit.Assert.*;

public class TeacherTest {

    @Test
    public void constructor_ShouldInitializeDefaults() {
        Teacher teacher = new Teacher();

        assertNotNull(teacher.getId());
        assertTrue(teacher.isActive());
        assertNotNull(teacher.getTeachingSubjects());
        assertTrue(teacher.getTeachingSubjects().isEmpty());
        assertNotNull(teacher.getAssignedGroups());
        assertTrue(teacher.getAssignedGroups().isEmpty());
    }

    @Test
    public void getRole_ShouldReturnTeacher() {
        Teacher teacher = new Teacher();

        assertEquals(User.UserRole.TEACHER, teacher.getRole());
    }

    @Test
    public void getFullName_ShouldReturnFormattedName() {
        Teacher teacher = new Teacher();
        teacher.setLastName("Petrov");
        teacher.setFirstName("Petr");
        teacher.setPatronymic("Petrovich");

        assertEquals("Petrov Petr Petrovich", teacher.getFullName());
    }

    @Test
    public void canSetGrades_ShouldReturnTrue_WhenTeacherTeachesSubjectAndGroup() {
        Teacher teacher = new Teacher();
        Subject subject = new Subject("MATH", "Mathematics");
        StudentGroup group = new StudentGroup("10A", "10A", 10);

        teacher.addTeachingSubject(subject);
        teacher.assignGroup(group);

        assertTrue(teacher.canSetGrades(subject, group));
    }

    @Test
    public void canSetGrades_ShouldReturnFalse_WhenNotTeachingSubject() {
        Teacher teacher = new Teacher();
        Subject subject = new Subject("MATH", "Mathematics");
        Subject otherSubject = new Subject("PHY", "Physics");
        StudentGroup group = new StudentGroup("10A", "10A", 10);

        teacher.addTeachingSubject(subject);
        teacher.assignGroup(group);

        assertFalse(teacher.canSetGrades(otherSubject, group));
    }

    @Test
    public void canSetGrades_ShouldReturnFalse_WhenNotAssignedToGroup() {
        Teacher teacher = new Teacher();
        Subject subject = new Subject("MATH", "Mathematics");
        StudentGroup group = new StudentGroup("10A", "10A", 10);
        StudentGroup otherGroup = new StudentGroup("11B", "11B", 11);

        teacher.addTeachingSubject(subject);
        teacher.assignGroup(group);

        assertFalse(teacher.canSetGrades(subject, otherGroup));
    }

    @Test
    public void canSetGrades_ShouldReturnFalse_WhenInactive() {
        Teacher teacher = new Teacher();
        Subject subject = new Subject("MATH", "Mathematics");
        StudentGroup group = new StudentGroup("10A", "10A", 10);

        teacher.addTeachingSubject(subject);
        teacher.assignGroup(group);
        teacher.setActive(false);

        assertFalse(teacher.canSetGrades(subject, group));
    }

    @Test
    public void canSetAttendance_ShouldReturnTrue_WhenAssignedAndActive() {
        Teacher teacher = new Teacher();
        StudentGroup group = new StudentGroup("10A", "10A", 10);

        teacher.assignGroup(group);

        assertTrue(teacher.canSetAttendance(group));
    }

    @Test
    public void canSetAttendance_ShouldReturnFalse_WhenNotAssigned() {
        Teacher teacher = new Teacher();
        StudentGroup group = new StudentGroup("10A", "10A", 10);

        assertFalse(teacher.canSetAttendance(group));
    }

    @Test
    public void removeTeachingSubject_ShouldRemoveSubject() {
        Teacher teacher = new Teacher();
        Subject subject = new Subject("MATH", "Mathematics");

        teacher.addTeachingSubject(subject);
        teacher.removeTeachingSubject(subject);

        assertFalse(teacher.teachesSubject(subject));
    }

    @Test
    public void unassignGroup_ShouldRemoveGroup() {
        Teacher teacher = new Teacher();
        StudentGroup group = new StudentGroup("10A", "10A", 10);

        teacher.assignGroup(group);
        teacher.unassignGroup(group);

        assertFalse(teacher.isAssignedToGroup(group));
    }

    @Test
    public void teachesSubject_ShouldReturnTrue_WhenSubjectInList() {
        Teacher teacher = new Teacher();
        Subject subject = new Subject("MATH", "Mathematics");

        teacher.addTeachingSubject(subject);

        assertTrue(teacher.teachesSubject(subject));
    }

    @Test
    public void isAssignedToGroup_ShouldReturnTrue_WhenGroupInList() {
        Teacher teacher = new Teacher();
        StudentGroup group = new StudentGroup("10A", "10A", 10);

        teacher.assignGroup(group);

        assertTrue(teacher.isAssignedToGroup(group));
    }

    @Test
    public void canAddHomework_ShouldDelegateToCanSetGrades() {
        Teacher teacher = new Teacher();
        Subject subject = new Subject("MATH", "Mathematics");
        StudentGroup group = new StudentGroup("10A", "10A", 10);

        teacher.addTeachingSubject(subject);
        teacher.assignGroup(group);

        assertTrue(teacher.canAddHomework(subject, group));
    }
}
