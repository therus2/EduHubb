package com.eduhab.service;

import com.eduhab.domain.StudentGroup;
import java.util.List;

public interface StudentGroupService {
    StudentGroup insert(StudentGroup studentGroup);
    StudentGroup getById(int id);
    List<StudentGroup> getAll();
    StudentGroup update(StudentGroup studentGroup);
    void deleteById(int id);
}
