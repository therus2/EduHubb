package com.eduhab.service;

import com.eduhab.domain.Grade;
import java.util.List;

public interface GradeService {
    Grade insert(Grade grade);
    Grade getById(int id);
    List<Grade> getAll();
    Grade update(Grade grade);
    void deleteById(int id);
    List<Grade> getByStudentId(int studentId);
    List<Grade> getByStudentIdAndSubjectId(int studentId, int subjectId);
    List<Grade> getByTeacherId(int teacherId);
    List<Grade> getByLessonId(int lessonId);
    List<Grade> getByStudentIdAndLessonId(int studentId, int lessonId);
    List<Grade> getByLessonIdAndStudentIdIn(int lessonId, List<Integer> studentIds);
}
