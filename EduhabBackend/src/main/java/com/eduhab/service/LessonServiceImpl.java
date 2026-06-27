package com.eduhab.service;

import com.eduhab.domain.Lesson;
import com.eduhab.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;

    @Override
    public List<Lesson> getAll() { return lessonRepository.findAll(); }

    @Override
    public Lesson insert(Lesson lesson) { return lessonRepository.save(lesson); }

    @Override
    public Lesson getById(int id) { return lessonRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lesson not found: " + id)); }

    @Override
    public Lesson update(Lesson lesson) { return lessonRepository.save(lesson); }

    @Override
    public void deleteById(int id) { lessonRepository.deleteById(id); }

    @Override
    public List<Lesson> getByGroupId(int groupId) { return lessonRepository.findByGroupId(groupId); }

    @Override
    public List<Lesson> getByGroupIdAndDayOfWeek(int groupId, int dayOfWeek) { return lessonRepository.findByGroupIdAndDayOfWeek(groupId, dayOfWeek); }

    @Override
    public List<Lesson> getByTeacherId(int teacherId) { return lessonRepository.findByTeacherId(teacherId); }

    @Override
    public List<Lesson> getByTeacherIdAndDayOfWeek(int teacherId, int dayOfWeek) { return lessonRepository.findByTeacherIdAndDayOfWeek(teacherId, dayOfWeek); }

    @Override
    public List<Lesson> getByGroupIdAndTeacherId(int groupId, int teacherId) { return lessonRepository.findByGroupIdAndTeacherId(groupId, teacherId); }
}
