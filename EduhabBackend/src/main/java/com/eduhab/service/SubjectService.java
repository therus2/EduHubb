package com.eduhab.service;

import com.eduhab.domain.Subject;
import java.util.List;

public interface SubjectService {
    Subject insert(Subject subject);
    Subject getById(int id);
    List<Subject> getAll();
    Subject update(Subject subject);
    void deleteById(int id);
}
