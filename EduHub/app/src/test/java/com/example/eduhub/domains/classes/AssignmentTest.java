package com.example.eduhub.domains.classes;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class AssignmentTest {

    @Test
    public void constructor_ShouldInitializeDefaults() {
        Assignment assignment = new Assignment();

        assertNotNull(assignment.getId());
        assertNotNull(assignment.getAssignedDate());
        assertNotNull(assignment.getCreatedAt());
    }

    @Test
    public void constructor_WithParams_ShouldSetFields() {
        String title = "Homework 1";
        Subject subject = new Subject("MATH", "Mathematics");
        Teacher teacher = new Teacher();
        StudentGroup group = new StudentGroup("10A", "10A", 10);
        LocalDate dueDate = LocalDate.of(2024, 10, 1);

        Assignment assignment = new Assignment(title, subject, teacher, group, dueDate);

        assertEquals(title, assignment.getTitle());
        assertEquals(subject, assignment.getSubject());
        assertEquals(teacher, assignment.getTeacher());
        assertEquals(group, assignment.getGroup());
        assertEquals(dueDate, assignment.getDueDate());
    }

    @Test
    public void isOverdue_ShouldReturnTrue_WhenDueDatePassed() {
        Assignment assignment = new Assignment();
        assignment.setDueDate(LocalDate.of(2020, 1, 1));

        assertTrue(assignment.isOverdue());
    }

    @Test
    public void isOverdue_ShouldReturnFalse_WhenDueDateInFuture() {
        Assignment assignment = new Assignment();
        assignment.setDueDate(LocalDate.now().plusDays(1));

        assertFalse(assignment.isOverdue());
    }

    @Test
    public void isOverdue_ShouldReturnFalse_WhenDueDateNull() {
        Assignment assignment = new Assignment();

        assertFalse(assignment.isOverdue());
    }

    @Test
    public void isDueSoon_ShouldReturnTrue_WhenWithinThreshold() {
        Assignment assignment = new Assignment();
        assignment.setDueDate(LocalDate.now().plusDays(2));

        assertTrue(assignment.isDueSoon(5));
    }

    @Test
    public void isDueSoon_ShouldReturnFalse_WhenOutsideThreshold() {
        Assignment assignment = new Assignment();
        assignment.setDueDate(LocalDate.now().plusDays(10));

        assertFalse(assignment.isDueSoon(5));
    }

    @Test
    public void isDueSoon_ShouldReturnFalse_WhenOverdue() {
        Assignment assignment = new Assignment();
        assignment.setDueDate(LocalDate.of(2020, 1, 1));

        assertFalse(assignment.isDueSoon(5));
    }

    @Test
    public void isDueSoon_ShouldReturnFalse_WhenDueDateNull() {
        Assignment assignment = new Assignment();

        assertFalse(assignment.isDueSoon(5));
    }

    @Test
    public void updateDescription_ShouldSetDescription() {
        Assignment assignment = new Assignment();

        assignment.updateDescription("New description");

        assertEquals("New description", assignment.getDescription());
    }

    @Test
    public void setMaxPoints_ShouldSetValue() {
        Assignment assignment = new Assignment();

        assignment.setMaxPoints(100);

        assertEquals(Integer.valueOf(100), assignment.getMaxPoints());
    }

    @Test
    public void assignmentTypeEnum_ShouldHaveExpectedValues() {
        assertNotNull(Assignment.AssignmentType.valueOf("HOMEWORK"));
        assertNotNull(Assignment.AssignmentType.valueOf("PROJECT"));
        assertNotNull(Assignment.AssignmentType.valueOf("LABORATORY"));
    }
}
