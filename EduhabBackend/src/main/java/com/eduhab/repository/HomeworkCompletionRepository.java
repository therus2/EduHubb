package com.eduhab.repository;

import com.eduhab.domain.HomeworkCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HomeworkCompletionRepository extends JpaRepository<HomeworkCompletion, Integer> {
    List<HomeworkCompletion> findByAssignmentId(int assignmentId);
    List<HomeworkCompletion> findByStudentId(int studentId);
    HomeworkCompletion findByAssignmentIdAndStudentId(int assignmentId, int studentId);
    List<HomeworkCompletion> findByAssignmentIdAndStatus(int assignmentId, String status);
}
