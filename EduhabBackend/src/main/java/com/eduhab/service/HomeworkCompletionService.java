package com.eduhab.service;

import com.eduhab.domain.HomeworkCompletion;
import java.util.List;

public interface HomeworkCompletionService {
    HomeworkCompletion insert(HomeworkCompletion homeworkCompletion);
    HomeworkCompletion getById(int id);
    List<HomeworkCompletion> getAll();
    HomeworkCompletion update(HomeworkCompletion homeworkCompletion);
    void deleteById(int id);
    List<HomeworkCompletion> getByAssignmentId(int assignmentId);
    List<HomeworkCompletion> getByStudentId(int studentId);
    HomeworkCompletion getByAssignmentIdAndStudentId(int assignmentId, int studentId);
    List<HomeworkCompletion> getByAssignmentIdAndStatus(int assignmentId, String status);
}
