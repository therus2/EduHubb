package com.eduhab.service;

import com.eduhab.domain.Student;
import java.util.List;

public interface StudentService {
    Student insert(Student student);
    Student getById(int id);
    List<Student> getAll();
    Student update(Student student);
    void deleteById(int id);
    List<Student> getByGroupId(int groupId);
}
