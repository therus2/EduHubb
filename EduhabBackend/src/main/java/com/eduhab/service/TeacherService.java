package com.eduhab.service;

import com.eduhab.domain.Teacher;
import java.util.List;

public interface TeacherService {
    Teacher insert(Teacher teacher);
    Teacher getById(int id);
    List<Teacher> getAll();
    Teacher update(Teacher teacher);
    void deleteById(int id);
}
