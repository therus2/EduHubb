package com.eduhab.repository;

import com.eduhab.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    List<Student> findByGroupId(int groupId);
    Student findByStudentIdNumber(String studentIdNumber);
    Student findByUserId(int userId);
}
