package com.eduhab.repository;

import com.eduhab.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    List<Attendance> findByStudentId(int studentId);
    List<Attendance> findByStudentIdAndDateBetween(int studentId, LocalDate start, LocalDate end);
    List<Attendance> findByLessonId(int lessonId);
    List<Attendance> findByStudentIdAndLessonId(int studentId, int lessonId);
    List<Attendance> findByLessonIdAndStudentIdIn(int lessonId, List<Integer> studentIds);
}
