package com.eduhab.service;

import com.eduhab.domain.HomeworkCompletion;
import com.eduhab.repository.HomeworkCompletionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HomeworkCompletionServiceImpl implements HomeworkCompletionService {
    private final HomeworkCompletionRepository homeworkCompletionRepository;

    @Override
    public List<HomeworkCompletion> getAll() { return homeworkCompletionRepository.findAll(); }

    @Override
    public HomeworkCompletion insert(HomeworkCompletion homeworkCompletion) { return homeworkCompletionRepository.save(homeworkCompletion); }

    @Override
    public HomeworkCompletion getById(int id) { return homeworkCompletionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("HomeworkCompletion not found: " + id)); }

    @Override
    public HomeworkCompletion update(HomeworkCompletion homeworkCompletion) { return homeworkCompletionRepository.save(homeworkCompletion); }

    @Override
    public void deleteById(int id) { homeworkCompletionRepository.deleteById(id); }

    @Override
    public List<HomeworkCompletion> getByAssignmentId(int assignmentId) { return homeworkCompletionRepository.findByAssignmentId(assignmentId); }

    @Override
    public List<HomeworkCompletion> getByStudentId(int studentId) { return homeworkCompletionRepository.findByStudentId(studentId); }

    @Override
    public HomeworkCompletion getByAssignmentIdAndStudentId(int assignmentId, int studentId) { return homeworkCompletionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId); }

    @Override
    public List<HomeworkCompletion> getByAssignmentIdAndStatus(int assignmentId, String status) { return homeworkCompletionRepository.findByAssignmentIdAndStatus(assignmentId, status); }
}
