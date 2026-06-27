package com.eduhab.service;

import com.eduhab.domain.StudentGroup;
import com.eduhab.repository.StudentGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentGroupServiceImpl implements StudentGroupService {
    private final StudentGroupRepository studentGroupRepository;

    @Override
    public List<StudentGroup> getAll() { return studentGroupRepository.findAll(); }

    @Override
    public StudentGroup insert(StudentGroup studentGroup) { return studentGroupRepository.save(studentGroup); }

    @Override
    public StudentGroup getById(int id) { return studentGroupRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("StudentGroup not found: " + id)); }

    @Override
    public StudentGroup update(StudentGroup studentGroup) { return studentGroupRepository.save(studentGroup); }

    @Override
    public void deleteById(int id) { studentGroupRepository.deleteById(id); }
}
