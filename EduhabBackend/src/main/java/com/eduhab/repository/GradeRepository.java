package com.eduhab.repository;

import com.eduhab.domain.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Integer> {
    List<Grade> findByStudentId(int studentId);
    List<Grade> findByStudentIdAndSubjectId(int studentId, int subjectId);
    List<Grade> findBySubjectId(int subjectId);
    List<Grade> findByTeacherId(int teacherId);
    List<Grade> findByLessonId(int lessonId);
    List<Grade> findByStudentIdAndLessonId(int studentId, int lessonId);
    List<Grade> findByLessonIdAndStudentIdIn(int lessonId, List<Integer> studentIds);
}
