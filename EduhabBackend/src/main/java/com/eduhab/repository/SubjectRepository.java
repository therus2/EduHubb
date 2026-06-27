package com.eduhab.repository;

import com.eduhab.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    Subject findByCode(String code);
}
