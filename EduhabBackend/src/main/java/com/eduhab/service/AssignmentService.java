package com.eduhab.service;

import com.eduhab.domain.Assignment;
import java.util.List;

public interface AssignmentService {
    Assignment insert(Assignment assignment);
    Assignment getById(int id);
    List<Assignment> getAll();
    Assignment update(Assignment assignment);
    void deleteById(int id);
    List<Assignment> getByGroupId(int groupId);
    List<Assignment> getByTeacherId(int teacherId);
    List<Assignment> getByGroupIdAndTeacherId(int groupId, int teacherId);
}
