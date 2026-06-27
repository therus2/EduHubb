package com.example.eduhub.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class GradeDisplayHelperTest {

    @Test
    public void formatJournalCell_ShouldReturnNumber_ForValidGrade() {
        assertEquals("4", GradeDisplayHelper.formatJournalCell(4, null));
    }

    @Test
    public void formatJournalCell_ShouldReturnN_ForAbsent() {
        assertEquals("Н", GradeDisplayHelper.formatJournalCell(0, "ABSENT"));
    }

    @Test
    public void formatJournalCell_ShouldReturnB_ForExempt() {
        assertEquals("Б", GradeDisplayHelper.formatJournalCell(0, "EXEMPT"));
    }

    @Test
    public void formatJournalCell_ShouldReturnZach_ForCreditPass() {
        assertEquals("Зач", GradeDisplayHelper.formatJournalCell(0, "CREDIT_PASS"));
    }

    @Test
    public void formatJournalCell_ShouldReturnEmpty_ForZeroValue() {
        assertEquals("", GradeDisplayHelper.formatJournalCell(0, null));
    }

    @Test
    public void formatJournalCell_ShouldReturnValue_ForPositiveNonGrade() {
        assertEquals("10", GradeDisplayHelper.formatJournalCell(10, null));
    }

    @Test
    public void countsForAverage_ShouldReturnTrue_ForValidGrade() {
        assertTrue(GradeDisplayHelper.countsForAverage(4, null));
    }

    @Test
    public void countsForAverage_ShouldReturnTrue_ForEdgeValues() {
        assertTrue(GradeDisplayHelper.countsForAverage(2, null));
        assertTrue(GradeDisplayHelper.countsForAverage(5, null));
    }

    @Test
    public void countsForAverage_ShouldReturnFalse_ForAbsent() {
        assertFalse(GradeDisplayHelper.countsForAverage(0, "ABSENT"));
    }

    @Test
    public void countsForAverage_ShouldReturnFalse_ForExempt() {
        assertFalse(GradeDisplayHelper.countsForAverage(0, "EXEMPT"));
    }

    @Test
    public void countsForAverage_ShouldReturnFalse_ForCreditPass() {
        assertFalse(GradeDisplayHelper.countsForAverage(0, "CREDIT_PASS"));
    }

    @Test
    public void countsForAverage_ShouldReturnFalse_ForInvalidValue() {
        assertFalse(GradeDisplayHelper.countsForAverage(1, null));
    }

    @Test
    public void countsForAverage_ShouldReturnFalse_ForHighValue() {
        assertFalse(GradeDisplayHelper.countsForAverage(100, null));
    }
}
