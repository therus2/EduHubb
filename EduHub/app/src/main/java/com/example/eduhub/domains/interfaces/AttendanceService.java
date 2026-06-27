package com.example.eduhub.domains.interfaces;

import com.example.eduhub.domains.classes.Attendance;
import com.example.eduhub.domains.classes.Student;
import com.example.eduhub.domains.classes.StudentGroup;
import com.example.eduhub.domains.classes.Teacher;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    Attendance markAttendance(Teacher teacher, Student student, LocalDate date,
                              Attendance.AttendanceStatus status, String comment);
    void updateAttendance(Attendance attendance, Attendance.AttendanceStatus newStatus);
    List<Attendance> getStudentAttendance(Student student);
    List<Attendance> getStudentAttendanceByDateRange(Student student, LocalDate from, LocalDate to);
    List<Attendance> getGroupAttendance(StudentGroup group, LocalDate date);
}