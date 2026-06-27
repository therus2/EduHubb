package com.eduhab.repository;

import com.eduhab.domain.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findByGroupId(int groupId);
    List<Assignment> findBySubjectId(int subjectId);
    List<Assignment> findByDueDateBefore(LocalDate date);
    List<Assignment> findByTeacherId(int teacherId);
    List<Assignment> findByGroupIdAndTeacherId(int groupId, int teacherId);
}
