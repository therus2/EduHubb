package com.eduhab.service;

import com.eduhab.domain.Teacher;
import com.eduhab.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;

    @Override
    public List<Teacher> getAll() { return teacherRepository.findAll(); }

    @Override
    public Teacher insert(Teacher teacher) { return teacherRepository.save(teacher); }

    @Override
    public Teacher getById(int id) { return teacherRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Teacher not found: " + id)); }

    @Override
    public Teacher update(Teacher teacher) { return teacherRepository.save(teacher); }

    @Override
    public void deleteById(int id) { teacherRepository.deleteById(id); }
}
