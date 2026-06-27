package com.eduhab.service;

import com.eduhab.domain.Student;
import com.eduhab.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    @Override
    public List<Student> getAll() { return studentRepository.findAll(); }

    @Override
    public Student insert(Student student) { return studentRepository.save(student); }

    @Override
    public Student getById(int id) { return studentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Student not found: " + id)); }

    @Override
    public Student update(Student student) { return studentRepository.save(student); }

    @Override
    public void deleteById(int id) { studentRepository.deleteById(id); }

    @Override
    public List<Student> getByGroupId(int groupId) { return studentRepository.findByGroupId(groupId); }
}
