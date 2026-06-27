package com.eduhab.service;

import com.eduhab.domain.Attendance;
import com.eduhab.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;

    @Override
    public List<Attendance> getAll() { return attendanceRepository.findAll(); }

    @Override
    public Attendance insert(Attendance attendance) { return attendanceRepository.save(attendance); }

    @Override
    public Attendance getById(int id) { return attendanceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Attendance not found: " + id)); }

    @Override
    public Attendance update(Attendance attendance) { return attendanceRepository.save(attendance); }

    @Override
    public void deleteById(int id) { attendanceRepository.deleteById(id); }

    @Override
    public List<Attendance> getByStudentId(int studentId) { return attendanceRepository.findByStudentId(studentId); }

    @Override
    public List<Attendance> getByStudentIdAndDateBetween(int studentId, LocalDate start, LocalDate end) { return attendanceRepository.findByStudentIdAndDateBetween(studentId, start, end); }

    @Override
    public List<Attendance> getByLessonId(int lessonId) { return attendanceRepository.findByLessonId(lessonId); }

    @Override
    public List<Attendance> getByStudentIdAndLessonId(int studentId, int lessonId) { return attendanceRepository.findByStudentIdAndLessonId(studentId, lessonId); }

    @Override
    public List<Attendance> getByLessonIdAndStudentIdIn(int lessonId, List<Integer> studentIds) { return attendanceRepository.findByLessonIdAndStudentIdIn(lessonId, studentIds); }
}
