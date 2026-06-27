package com.example.eduhub.domains.interfaces;

import com.example.eduhub.domains.classes.Grade;
import com.example.eduhub.domains.classes.Student;
import com.example.eduhub.domains.classes.StudentGroup;
import com.example.eduhub.domains.classes.Subject;
import com.example.eduhub.domains.classes.Teacher;

import java.util.List;

public interface GradeService {
    Grade setGrade(Teacher teacher, Student student, Subject subject, Integer value,
                   Integer weight, Grade.GradeType type, String comment);
    void updateGrade(Grade grade, Integer newValue, Integer weight, String comment, Teacher teacher);
    void deleteGrade(Grade grade);
    List<Grade> getStudentGrades(Student student);
    List<Grade> getStudentGradesBySubject(Student student, Subject subject);
    List<Grade> getGroupGrades(StudentGroup group, Subject subject);
}