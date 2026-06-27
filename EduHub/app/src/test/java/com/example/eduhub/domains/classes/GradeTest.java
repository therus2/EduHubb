package com.example.eduhub.domains.classes;

import org.junit.Test;

import static org.junit.Assert.*;

public class GradeTest {

    @Test
    public void constructor_ShouldInitializeId() {
        Grade grade = new Grade();

        assertNotNull(grade.getId());
    }

    @Test
    public void constructor_WithParams_ShouldSetFields() {
        Student student = new Student();
        Subject subject = new Subject("MATH", "Math");
        Teacher teacher = new Teacher();

        Grade grade = new Grade(student, subject, teacher, 4, Grade.GradeType.CURRENT);

        assertEquals(student, grade.getStudent());
        assertEquals(subject, grade.getSubject());
        assertEquals(teacher, grade.getTeacher());
        assertEquals(Integer.valueOf(4), grade.getValue());
        assertEquals(Grade.GradeType.CURRENT, grade.getType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setValue_ShouldThrow_WhenValueLessThan2() {
        Grade grade = new Grade();
        grade.setValue(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setValue_ShouldThrow_WhenValueGreaterThan5() {
        Grade grade = new Grade();
        grade.setValue(6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setValue_ShouldThrow_WhenValueNull() {
        Grade grade = new Grade();
        grade.setValue(null);
    }

    @Test
    public void setValue_ShouldAcceptValidRange() {
        Grade grade = new Grade();

        for (int v = 2; v <= 5; v++) {
            grade.setValue(v);
            assertEquals(Integer.valueOf(v), grade.getValue());
        }
    }

    @Test
    public void isPassing_ShouldReturnTrue_WhenValue3OrMore() {
        Grade grade = new Grade();
        grade.setValue(3);

        assertTrue(grade.isPassing());
    }

    @Test
    public void isPassing_ShouldReturnFalse_WhenValue2() {
        Grade grade = new Grade();
        grade.setValue(2);

        assertFalse(grade.isPassing());
    }

    @Test
    public void addComment_ShouldSetComment() {
        Grade grade = new Grade();

        grade.addComment("Good work");

        assertEquals("Good work", grade.getComment());
    }

    @Test
    public void setWeight_ShouldSetWeight() {
        Grade grade = new Grade();

        grade.setWeight(2);

        assertEquals(Integer.valueOf(2), grade.getWeight());
    }

    @Test
    public void defaultWeight_ShouldBe1() {
        Grade grade = new Grade();

        assertEquals(Integer.valueOf(1), grade.getWeight());
    }

    @Test
    public void gradeTypeEnum_ShouldHaveExpectedValues() {
        assertNotNull(Grade.GradeType.valueOf("CURRENT"));
        assertNotNull(Grade.GradeType.valueOf("CONTROL"));
        assertNotNull(Grade.GradeType.valueOf("EXAM"));
    }
}
