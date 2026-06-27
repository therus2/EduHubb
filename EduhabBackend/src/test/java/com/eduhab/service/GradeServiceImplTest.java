package com.eduhab.service;

import com.eduhab.domain.Grade;
import com.eduhab.repository.GradeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceImplTest {

    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private GradeServiceImpl gradeService;

    @Test
    void getAll_ShouldReturnAllGrades() {
        when(gradeRepository.findAll()).thenReturn(List.of(new Grade(), new Grade()));

        List<Grade> result = gradeService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void getById_ShouldReturnGrade_WhenExists() {
        Grade grade = new Grade();
        grade.setId(1);
        grade.setValue(4);
        when(gradeRepository.findById(1)).thenReturn(Optional.of(grade));

        Grade result = gradeService.getById(1);

        assertEquals(1, result.getId());
        assertEquals(4, result.getValue());
    }

    @Test
    void getById_ShouldThrow_WhenNotFound() {
        when(gradeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> gradeService.getById(99));
    }

    @Test
    void insert_ShouldSaveAndReturn() {
        Grade grade = new Grade();
        grade.setValue(5);
        when(gradeRepository.save(grade)).thenReturn(grade);

        Grade result = gradeService.insert(grade);

        assertEquals(5, result.getValue());
        verify(gradeRepository).save(grade);
    }

    @Test
    void update_ShouldSaveAndReturn() {
        Grade grade = new Grade();
        grade.setId(1);
        grade.setValue(3);
        when(gradeRepository.save(grade)).thenReturn(grade);

        Grade result = gradeService.update(grade);

        assertEquals(3, result.getValue());
        verify(gradeRepository).save(grade);
    }

    @Test
    void deleteById_ShouldCallRepository() {
        gradeService.deleteById(1);

        verify(gradeRepository).deleteById(1);
    }

    @Test
    void getByStudentId_ShouldReturnGrades() {
        when(gradeRepository.findByStudentId(1)).thenReturn(List.of(new Grade()));

        List<Grade> result = gradeService.getByStudentId(1);

        assertEquals(1, result.size());
    }

    @Test
    void getByStudentIdAndSubjectId_ShouldReturnGrades() {
        when(gradeRepository.findByStudentIdAndSubjectId(1, 2)).thenReturn(List.of(new Grade(), new Grade()));

        List<Grade> result = gradeService.getByStudentIdAndSubjectId(1, 2);

        assertEquals(2, result.size());
    }

    @Test
    void getByTeacherId_ShouldReturnGrades() {
        when(gradeRepository.findByTeacherId(1)).thenReturn(List.of(new Grade()));

        List<Grade> result = gradeService.getByTeacherId(1);

        assertEquals(1, result.size());
    }

    @Test
    void getByLessonId_ShouldReturnGrades() {
        when(gradeRepository.findByLessonId(1)).thenReturn(List.of(new Grade()));

        List<Grade> result = gradeService.getByLessonId(1);

        assertEquals(1, result.size());
    }

    @Test
    void getByLessonIdAndStudentIdIn_ShouldReturnGrades() {
        when(gradeRepository.findByLessonIdAndStudentIdIn(1, List.of(1, 2)))
                .thenReturn(List.of(new Grade(), new Grade(), new Grade()));

        List<Grade> result = gradeService.getByLessonIdAndStudentIdIn(1, List.of(1, 2));

        assertEquals(3, result.size());
    }
}
