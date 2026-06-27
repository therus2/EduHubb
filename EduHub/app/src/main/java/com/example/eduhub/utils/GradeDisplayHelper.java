package com.example.eduhub.utils;


public final class GradeDisplayHelper {

    private GradeDisplayHelper() {
    }

    
    public static String formatJournalCell(int value, String gradeType) {
        if (gradeType != null) {
            switch (gradeType) {
                case "ABSENT":
                    return "\u041D";
                case "EXEMPT":
                    return "\u0411";
                case "CREDIT_PASS":
                    return "\u0417\u0430\u0447";
                default:
                    break;
            }
        }
        if (value >= 2 && value <= 5) {
            return String.valueOf(value);
        }
        if (value > 0) {
            return String.valueOf(value);
        }
        return "";
    }

    
    public static boolean countsForAverage(int value, String gradeType) {
        if (gradeType != null) {
            switch (gradeType) {
                case "ABSENT":
                case "EXEMPT":
                case "CREDIT_PASS":
                    return false;
                default:
                    break;
            }
        }
        return value >= 2 && value <= 5;
    }
}
