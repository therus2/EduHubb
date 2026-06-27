package com.eduhab.repository;

import com.eduhab.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class GradeRepositoryTest {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private EntityManager em;

    private Student student;
    private Subject subject;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        User studentUser = User.builder()
                .email("student@test.com").passwordHash("hash")
                .firstName("Stu").lastName("Dent").role("STUDENT")
                .build();
        em.persist(studentUser);

        User teacherUser = User.builder()
                .email("teacher@test.com").passwordHash("hash")
                .firstName("Tea").lastName("Cher").role("TEACHER")
                .build();
        em.persist(teacherUser);

        StudentGroup group = StudentGroup.builder()
                .name("Test Group").code("TG-01").courseNumber(10).build();
        em.persist(group);

        subject = Subject.builder()
                .name("Mathematics").code("MATH").build();
        em.persist(subject);

        teacher = Teacher.builder()
                .user(teacherUser)
                .employeeId("T001")
                .build();
        em.persist(teacher);

        student = Student.builder()
                .user(studentUser)
                .group(group)
                .studentIdNumber("S001")
                .build();
        em.persist(student);
        em.flush();
    }

    @Test
    void findByStudentId_ShouldReturnGrades() {
        Grade grade = Grade.builder()
                .student(student).subject(subject).teacher(teacher)
                .value(4).gradeType("CURRENT")
                .build();
        gradeRepository.save(grade);

        List<Grade> grades = gradeRepository.findByStudentId(student.getId());

        assertEquals(1, grades.size());
        assertEquals(4, grades.get(0).getValue());
    }

    @Test
    void findByStudentIdAndSubjectId_ShouldReturnFilteredGrades() {
        Grade grade1 = Grade.builder()
                .student(student).subject(subject).teacher(teacher)
                .value(5).gradeType("CURRENT")
                .build();
        Grade grade2 = Grade.builder()
                .student(student).subject(subject).teacher(teacher)
                .value(3).gradeType("CONTROL")
                .build();
        gradeRepository.save(grade1);
        gradeRepository.save(grade2);

        List<Grade> grades = gradeRepository.findByStudentIdAndSubjectId(student.getId(), subject.getId());

        assertEquals(2, grades.size());
    }

    @Test
    void findByLessonId_ShouldReturnGrades() {
        Lesson lesson = Lesson.builder()
                .group(student.getGroup()).subject(subject).teacher(teacher)
                .dayOfWeek(1)
                .startTime(java.time.LocalTime.of(9, 0))
                .endTime(java.time.LocalTime.of(9, 45))
                .build();
        em.persist(lesson);
        em.flush();

        Grade grade = Grade.builder()
                .student(student).subject(subject).teacher(teacher)
                .value(4).gradeType("CURRENT")
                .lesson(lesson)
                .build();
        gradeRepository.save(grade);

        List<Grade> grades = gradeRepository.findByLessonId(lesson.getId());

        assertEquals(1, grades.size());
    }

    @Test
    void findByLessonIdAndStudentIdIn_ShouldReturnGrades_WhenStudentIdsMatch() {
        Lesson lesson = Lesson.builder()
                .group(student.getGroup()).subject(subject).teacher(teacher)
                .dayOfWeek(2)
                .startTime(java.time.LocalTime.of(10, 0))
                .endTime(java.time.LocalTime.of(10, 45))
                .build();
        em.persist(lesson);
        em.flush();

        Grade grade = Grade.builder()
                .student(student).subject(subject).teacher(teacher)
                .value(5).gradeType("CURRENT")
                .lesson(lesson)
                .build();
        gradeRepository.save(grade);

        List<Grade> grades = gradeRepository.findByLessonIdAndStudentIdIn(
                lesson.getId(), List.of(student.getId()));

        assertEquals(1, grades.size());
        assertEquals(5, grades.get(0).getValue());
    }
}
