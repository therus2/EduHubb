package com.eduhab.service;

import com.eduhab.domain.Lesson;
import java.util.List;

public interface LessonService {
    Lesson insert(Lesson lesson);
    Lesson getById(int id);
    List<Lesson> getAll();
    Lesson update(Lesson lesson);
    void deleteById(int id);
    List<Lesson> getByGroupId(int groupId);
    List<Lesson> getByGroupIdAndDayOfWeek(int groupId, int dayOfWeek);
    List<Lesson> getByTeacherId(int teacherId);
    List<Lesson> getByTeacherIdAndDayOfWeek(int teacherId, int dayOfWeek);
    List<Lesson> getByGroupIdAndTeacherId(int groupId, int teacherId);
}
