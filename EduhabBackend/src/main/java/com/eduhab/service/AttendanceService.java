package com.eduhab.service;

import com.eduhab.domain.Attendance;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    Attendance insert(Attendance attendance);
    Attendance getById(int id);
    List<Attendance> getAll();
    Attendance update(Attendance attendance);
    void deleteById(int id);
    List<Attendance> getByStudentId(int studentId);
    List<Attendance> getByStudentIdAndDateBetween(int studentId, LocalDate start, LocalDate end);
    List<Attendance> getByLessonId(int lessonId);
    List<Attendance> getByStudentIdAndLessonId(int studentId, int lessonId);
    List<Attendance> getByLessonIdAndStudentIdIn(int lessonId, List<Integer> studentIds);
}
