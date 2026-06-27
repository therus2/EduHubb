package com.example.eduhub.utils;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class LearningPeriodHelperTest {

    @Test
    public void getPeriodLabel_ShouldReturnFirstSemester_ForAutumn() {
        String result = LearningPeriodHelper.getPeriodLabel("semester");

        int month = LocalDate.now().getMonthValue();
        if (month >= 9 && month <= 12) {
            assertEquals("1 полугодие", result);
        } else {
            assertEquals("2 полугодие", result);
        }
    }

    @Test
    public void getPeriodLabel_ShouldReturnFirstTrimester_ForSeptember() {
        String result = LearningPeriodHelper.getPeriodLabel("trimester");

        int month = LocalDate.now().getMonthValue();
        if (month >= 9 && month <= 10) {
            assertEquals("1 триместр", result);
        } else if (month >= 11 && month <= 12) {
            assertEquals("2 триместр", result);
        } else {
            assertEquals("3 триместр", result);
        }
    }

    @Test
    public void getPeriodDates_ShouldReturnNonEmpty() {
        String semesterDates = LearningPeriodHelper.getPeriodDates("semester");
        String trimesterDates = LearningPeriodHelper.getPeriodDates("trimester");

        assertNotNull(semesterDates);
        assertFalse(semesterDates.isEmpty());
        assertNotNull(trimesterDates);
        assertFalse(trimesterDates.isEmpty());
    }

    @Test
    public void getPeriodDates_ShouldContainYear() {
        String result = LearningPeriodHelper.getPeriodDates("semester");
        String year = String.valueOf(LocalDate.now().getYear());

        assertTrue(result.contains(year));
    }

    @Test
    public void getPeriodStart_ShouldReturnNonNull() {
        assertNotNull(LearningPeriodHelper.getPeriodStart("semester"));
        assertNotNull(LearningPeriodHelper.getPeriodStart("trimester"));
    }

    @Test
    public void getPeriodStart_ShouldBeBeforePeriodEnd() {
        LocalDate start = LearningPeriodHelper.getPeriodStart("semester");
        LocalDate end = LearningPeriodHelper.getPeriodEnd("semester");

        assertTrue(start.isBefore(end) || start.equals(end));
    }

    @Test
    public void getPeriodEnd_ShouldReturnNonNull() {
        assertNotNull(LearningPeriodHelper.getPeriodEnd("semester"));
        assertNotNull(LearningPeriodHelper.getPeriodEnd("trimester"));
    }

    @Test
    public void getPeriodEnd_ShouldBeAfterPeriodStart() {
        LocalDate end = LearningPeriodHelper.getPeriodEnd("semester");
        LocalDate start = LearningPeriodHelper.getPeriodStart("semester");

        assertTrue(end.isAfter(start) || end.equals(start));
    }

    @Test
    public void getPeriodStart_ShouldReturnSeptember1_ForAutumnSemester() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 9 && month <= 12) {
            LocalDate start = LearningPeriodHelper.getPeriodStart("semester");
            assertEquals(LocalDate.of(LocalDate.now().getYear(), 9, 1), start);
        }
    }
}
