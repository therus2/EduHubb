package com.eduhab.repository;

import com.eduhab.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    Teacher findByEmployeeId(String employeeId);
    Teacher findByUserId(int userId);
}
