package com.eduhab.repository;

import com.eduhab.domain.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByGroupId(int groupId);
    List<Lesson> findByGroupIdAndDayOfWeek(int groupId, int dayOfWeek);
    List<Lesson> findByTeacherId(int teacherId);
    List<Lesson> findByTeacherIdAndDayOfWeek(int teacherId, int dayOfWeek);
    List<Lesson> findByGroupIdAndTeacherId(int groupId, int teacherId);
}
