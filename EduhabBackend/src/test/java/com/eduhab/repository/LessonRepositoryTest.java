package com.eduhab.repository;

import com.eduhab.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class LessonRepositoryTest {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private EntityManager em;

    private StudentGroup group;
    private Subject subject;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        User teacherUser = User.builder()
                .email("tchr@test.com").passwordHash("hash")
                .firstName("Test").lastName("Teacher").role("TEACHER")
                .build();
        em.persist(teacherUser);

        group = StudentGroup.builder().name("10A").code("10A").courseNumber(10).build();
        em.persist(group);

        subject = Subject.builder().name("Physics").code("PHY").build();
        em.persist(subject);

        teacher = Teacher.builder()
                .user(teacherUser)
                .employeeId("T002")
                .build();
        em.persist(teacher);
        em.flush();
    }

    @Test
    void findByGroupId_ShouldReturnLessons() {
        Lesson lesson = Lesson.builder()
                .group(group).subject(subject).teacher(teacher)
                .dayOfWeek(1)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(8, 45))
                .build();
        lessonRepository.save(lesson);

        List<Lesson> lessons = lessonRepository.findByGroupId(group.getId());

        assertEquals(1, lessons.size());
    }

    @Test
    void findByGroupIdAndDayOfWeek_ShouldReturnFiltered() {
        Lesson lesson1 = Lesson.builder()
                .group(group).subject(subject).teacher(teacher)
                .dayOfWeek(1)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(8, 45))
                .build();
        Lesson lesson2 = Lesson.builder()
                .group(group).subject(subject).teacher(teacher)
                .dayOfWeek(2)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 45))
                .build();
        lessonRepository.save(lesson1);
        lessonRepository.save(lesson2);

        List<Lesson> lessons = lessonRepository.findByGroupIdAndDayOfWeek(group.getId(), 1);

        assertEquals(1, lessons.size());
        assertEquals(1, lessons.get(0).getDayOfWeek());
    }

    @Test
    void findByTeacherId_ShouldReturnLessons() {
        Lesson lesson = Lesson.builder()
                .group(group).subject(subject).teacher(teacher)
                .dayOfWeek(3)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(10, 45))
                .build();
        lessonRepository.save(lesson);

        List<Lesson> lessons = lessonRepository.findByTeacherId(teacher.getId());

        assertEquals(1, lessons.size());
    }

    @Test
    void findByGroupIdAndTeacherId_ShouldReturnLessons() {
        Lesson lesson = Lesson.builder()
                .group(group).subject(subject).teacher(teacher)
                .dayOfWeek(4)
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(11, 45))
                .build();
        lessonRepository.save(lesson);

        List<Lesson> lessons = lessonRepository.findByGroupIdAndTeacherId(group.getId(), teacher.getId());

        assertEquals(1, lessons.size());
    }

    @Test
    void findByTeacherIdAndDayOfWeek_ShouldReturnFiltered() {
        Lesson lesson = Lesson.builder()
                .group(group).subject(subject).teacher(teacher)
                .dayOfWeek(5)
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(12, 45))
                .build();
        lessonRepository.save(lesson);

        List<Lesson> lessons = lessonRepository.findByTeacherIdAndDayOfWeek(teacher.getId(), 5);

        assertEquals(1, lessons.size());
    }
}
