package com.eduhab.service;

import com.eduhab.domain.Assignment;
import com.eduhab.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentRepository assignmentRepository;

    @Override
    public List<Assignment> getAll() { return assignmentRepository.findAll(); }

    @Override
    public Assignment insert(Assignment assignment) { return assignmentRepository.save(assignment); }

    @Override
    public Assignment getById(int id) { return assignmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Assignment not found: " + id)); }

    @Override
    public Assignment update(Assignment assignment) { return assignmentRepository.save(assignment); }

    @Override
    public void deleteById(int id) { assignmentRepository.deleteById(id); }

    @Override
    public List<Assignment> getByGroupId(int groupId) { return assignmentRepository.findByGroupId(groupId); }

    @Override
    public List<Assignment> getByTeacherId(int teacherId) { return assignmentRepository.findByTeacherId(teacherId); }

    @Override
    public List<Assignment> getByGroupIdAndTeacherId(int groupId, int teacherId) { return assignmentRepository.findByGroupIdAndTeacherId(groupId, teacherId); }
}
