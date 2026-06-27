package com.eduhab.service;

import com.eduhab.domain.Grade;
import com.eduhab.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {
    private final GradeRepository gradeRepository;

    @Override
    public List<Grade> getAll() { return gradeRepository.findAll(); }

    @Override
    public Grade insert(Grade grade) { return gradeRepository.save(grade); }

    @Override
    public Grade getById(int id) { return gradeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Grade not found: " + id)); }

    @Override
    public Grade update(Grade grade) { return gradeRepository.save(grade); }

    @Override
    public void deleteById(int id) { gradeRepository.deleteById(id); }

    @Override
    public List<Grade> getByStudentId(int studentId) { return gradeRepository.findByStudentId(studentId); }

    @Override
    public List<Grade> getByStudentIdAndSubjectId(int studentId, int subjectId) { return gradeRepository.findByStudentIdAndSubjectId(studentId, subjectId); }

    @Override
    public List<Grade> getByTeacherId(int teacherId) { return gradeRepository.findByTeacherId(teacherId); }

    @Override
    public List<Grade> getByLessonId(int lessonId) { return gradeRepository.findByLessonId(lessonId); }

    @Override
    public List<Grade> getByStudentIdAndLessonId(int studentId, int lessonId) { return gradeRepository.findByStudentIdAndLessonId(studentId, lessonId); }

    @Override
    public List<Grade> getByLessonIdAndStudentIdIn(int lessonId, List<Integer> studentIds) { return gradeRepository.findByLessonIdAndStudentIdIn(lessonId, studentIds); }
}
