package com.eduhab.service;

import com.eduhab.domain.Lesson;
import com.eduhab.repository.LessonRepository;
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
class LessonServiceImplTest {

    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private LessonServiceImpl lessonService;

    @Test
    void getAll_ShouldReturnAllLessons() {
        when(lessonRepository.findAll()).thenReturn(List.of(new Lesson(), new Lesson()));

        List<Lesson> result = lessonService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void getById_ShouldReturnLesson_WhenExists() {
        Lesson lesson = new Lesson();
        lesson.setId(1);
        lesson.setDayOfWeek(1);
        when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));

        Lesson result = lessonService.getById(1);

        assertEquals(1, result.getDayOfWeek());
    }

    @Test
    void getById_ShouldThrow_WhenNotFound() {
        when(lessonRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> lessonService.getById(99));
    }

    @Test
    void insert_ShouldSaveAndReturn() {
        Lesson lesson = new Lesson();
        when(lessonRepository.save(lesson)).thenReturn(lesson);

        Lesson result = lessonService.insert(lesson);

        assertNotNull(result);
        verify(lessonRepository).save(lesson);
    }

    @Test
    void update_ShouldSaveAndReturn() {
        Lesson lesson = new Lesson();
        when(lessonRepository.save(lesson)).thenReturn(lesson);

        Lesson result = lessonService.update(lesson);

        assertNotNull(result);
        verify(lessonRepository).save(lesson);
    }

    @Test
    void deleteById_ShouldCallRepository() {
        lessonService.deleteById(1);

        verify(lessonRepository).deleteById(1);
    }

    @Test
    void getByGroupId_ShouldReturnLessons() {
        when(lessonRepository.findByGroupId(1)).thenReturn(List.of(new Lesson()));

        List<Lesson> result = lessonService.getByGroupId(1);

        assertEquals(1, result.size());
    }

    @Test
    void getByGroupIdAndDayOfWeek_ShouldReturnLessons() {
        when(lessonRepository.findByGroupIdAndDayOfWeek(1, 2)).thenReturn(List.of(new Lesson()));

        List<Lesson> result = lessonService.getByGroupIdAndDayOfWeek(1, 2);

        assertEquals(1, result.size());
    }

    @Test
    void getByTeacherId_ShouldReturnLessons() {
        when(lessonRepository.findByTeacherId(1)).thenReturn(List.of(new Lesson(), new Lesson()));

        List<Lesson> result = lessonService.getByTeacherId(1);

        assertEquals(2, result.size());
    }

    @Test
    void getByTeacherIdAndDayOfWeek_ShouldReturnLessons() {
        when(lessonRepository.findByTeacherIdAndDayOfWeek(1, 3)).thenReturn(List.of());

        List<Lesson> result = lessonService.getByTeacherIdAndDayOfWeek(1, 3);

        assertTrue(result.isEmpty());
    }

    @Test
    void getByGroupIdAndTeacherId_ShouldReturnLessons() {
        when(lessonRepository.findByGroupIdAndTeacherId(1, 1)).thenReturn(List.of(new Lesson()));

        List<Lesson> result = lessonService.getByGroupIdAndTeacherId(1, 1);

        assertEquals(1, result.size());
    }
}
