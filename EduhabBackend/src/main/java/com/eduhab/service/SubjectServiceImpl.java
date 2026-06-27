package com.eduhab.service;

import com.eduhab.domain.Subject;
import com.eduhab.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;

    @Override
    public List<Subject> getAll() { return subjectRepository.findAll(); }

    @Override
    public Subject insert(Subject subject) { return subjectRepository.save(subject); }

    @Override
    public Subject getById(int id) { return subjectRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Subject not found: " + id)); }

    @Override
    public Subject update(Subject subject) { return subjectRepository.save(subject); }

    @Override
    public void deleteById(int id) { subjectRepository.deleteById(id); }
}
