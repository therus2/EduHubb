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
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EntityManager em;

    private StudentGroup group;

    @BeforeEach
    void setUp() {
        group = StudentGroup.builder().name("7A").code("7A").courseNumber(7).build();
        em.persist(group);

        User user1 = User.builder()
                .email("s1@test.com").passwordHash("hash")
                .firstName("Alice").lastName("Smith").role("STUDENT")
                .build();
        em.persist(user1);

        User user2 = User.builder()
                .email("s2@test.com").passwordHash("hash")
                .firstName("Bob").lastName("Jones").role("STUDENT")
                .build();
        em.persist(user2);

        Student student1 = Student.builder()
                .user(user1).group(group)
                .studentIdNumber("S001")
                .build();
        Student student2 = Student.builder()
                .user(user2).group(group)
                .studentIdNumber("S002")
                .build();
        em.persist(student1);
        em.persist(student2);
        em.flush();
    }

    @Test
    void findByGroupId_ShouldReturnStudentsInGroup() {
        List<Student> students = studentRepository.findByGroupId(group.getId());

        assertEquals(2, students.size());
    }

    @Test
    void findByGroupId_ShouldReturnEmpty_WhenNoStudents() {
        StudentGroup emptyGroup = StudentGroup.builder().name("ZZZ").code("ZZZ").courseNumber(1).build();
        em.persist(emptyGroup);
        em.flush();

        List<Student> students = studentRepository.findByGroupId(emptyGroup.getId());

        assertTrue(students.isEmpty());
    }

    @Test
    void findByUserId_ShouldReturnStudent() {
        List<Student> all = studentRepository.findAll();
        Student student = all.get(0);

        Student found = studentRepository.findByUserId(student.getUser().getId());

        assertNotNull(found);
    }

    @Test
    void findByStudentIdNumber_ShouldReturnStudent() {
        Student found = studentRepository.findByStudentIdNumber("S001");

        assertNotNull(found);
        assertEquals("S001", found.getStudentIdNumber());
    }
}
