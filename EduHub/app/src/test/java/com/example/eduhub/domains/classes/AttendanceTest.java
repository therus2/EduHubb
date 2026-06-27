package com.example.eduhub.domains.classes;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class AttendanceTest {

    @Test
    public void constructor_ShouldInitializeId() {
        Attendance attendance = new Attendance();

        assertNotNull(attendance.getId());
    }

    @Test
    public void constructor_WithParams_ShouldSetFields() {
        Student student = new Student();
        LocalDate date = LocalDate.of(2024, 9, 1);

        Attendance attendance = new Attendance(student, date, Attendance.AttendanceStatus.PRESENT);

        assertEquals(student, attendance.getStudent());
        assertEquals(date, attendance.getDate());
        assertEquals(Attendance.AttendanceStatus.PRESENT, attendance.getStatus());
    }

    @Test
    public void markStatus_ShouldUpdateStatus() {
        Attendance attendance = new Attendance();
        attendance.markStatus(Attendance.AttendanceStatus.ABSENT);

        assertEquals(Attendance.AttendanceStatus.ABSENT, attendance.getStatus());
    }

    @Test
    public void isAbsent_ShouldReturnTrue_ForAbsent() {
        Attendance attendance = new Attendance();
        attendance.setStatus(Attendance.AttendanceStatus.ABSENT);

        assertTrue(attendance.isAbsent());
    }

    @Test
    public void isAbsent_ShouldReturnTrue_ForLate() {
        Attendance attendance = new Attendance();
        attendance.setStatus(Attendance.AttendanceStatus.LATE);

        assertTrue(attendance.isAbsent());
    }

    @Test
    public void isAbsent_ShouldReturnTrue_ForExcusedAbsent() {
        Attendance attendance = new Attendance();
        attendance.setStatus(Attendance.AttendanceStatus.EXCUSED_ABSENT);

        assertTrue(attendance.isAbsent());
    }

    @Test
    public void isAbsent_ShouldReturnFalse_ForPresent() {
        Attendance attendance = new Attendance();
        attendance.setStatus(Attendance.AttendanceStatus.PRESENT);

        assertFalse(attendance.isAbsent());
    }

    @Test
    public void isAbsent_ShouldReturnFalse_ForEarlyLeave() {
        Attendance attendance = new Attendance();
        attendance.setStatus(Attendance.AttendanceStatus.EARLY_LEAVE);

        assertFalse(attendance.isAbsent());
    }

    @Test
    public void setComment_ShouldWork() {
        Attendance attendance = new Attendance();

        attendance.setComment("Was sick");

        assertEquals("Was sick", attendance.getComment());
    }

    @Test
    public void attendanceStatusEnum_ShouldHaveExpectedValues() {
        assertNotNull(Attendance.AttendanceStatus.valueOf("PRESENT"));
        assertNotNull(Attendance.AttendanceStatus.valueOf("ABSENT"));
        assertNotNull(Attendance.AttendanceStatus.valueOf("LATE"));
    }
}
