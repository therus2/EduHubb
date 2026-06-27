package com.example.eduhub.utils;

import java.time.LocalDate;
import java.time.Month;

public class LearningPeriodHelper {

    public static String getPeriodLabel(String system) {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();

        if ("semester".equals(system)) {
            if (month >= 9 && month <= 12) {
                return "1 полугодие";
            } else {
                return "2 полугодие";
            }
        } else {
            if (month >= 9 && month <= 10) {
                return "1 триместр";
            } else if (month >= 11 && month <= 12) {
                return "2 триместр";
            } else {
                return "3 триместр";
            }
        }
    }

    public static String getPeriodDates(String system) {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        if ("semester".equals(system)) {
            if (month >= 9 && month <= 12) {
                return "Сентябрь \u2014 Декабрь " + year;
            } else {
                return "Январь \u2014 Май " + year;
            }
        } else {
            if (month >= 9 && month <= 10) {
                return "Сентябрь \u2014 Октябрь " + year;
            } else if (month >= 11 && month <= 12) {
                return "Ноябрь \u2014 Декабрь " + year;
            } else {
                return "Февраль \u2014 Май " + year;
            }
        }
    }

    public static LocalDate getPeriodStart(String system) {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        if ("semester".equals(system)) {
            if (month >= 9 && month <= 12) {
                return LocalDate.of(year, Month.SEPTEMBER, 1);
            } else {
                return LocalDate.of(year, Month.JANUARY, 1);
            }
        } else {
            if (month >= 9 && month <= 10) {
                return LocalDate.of(year, Month.SEPTEMBER, 1);
            } else if (month >= 11 && month <= 12) {
                return LocalDate.of(year, Month.NOVEMBER, 1);
            } else {
                return LocalDate.of(year, Month.FEBRUARY, 1);
            }
        }
    }

    public static LocalDate getPeriodEnd(String system) {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        if ("semester".equals(system)) {
            if (month >= 9 && month <= 12) {
                return LocalDate.of(year, Month.DECEMBER, 31);
            } else {
                return LocalDate.of(year, Month.MAY, 31);
            }
        } else {
            if (month >= 9 && month <= 10) {
                return LocalDate.of(year, Month.OCTOBER, 31);
            } else if (month >= 11 && month <= 12) {
                return LocalDate.of(year, Month.DECEMBER, 31);
            } else {
                return LocalDate.of(year, Month.MAY, 31);
            }
        }
    }
}
