package com.eduhab.repository;

import com.eduhab.domain.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, Integer> {
    StudentGroup findByCode(String code);
}
