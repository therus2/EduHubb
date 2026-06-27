package com.example.eduhub.domains.interfaces;

import com.example.eduhub.domains.classes.Assignment;
import com.example.eduhub.domains.classes.HomeworkCompletion;
import com.example.eduhub.domains.classes.Student;
import com.example.eduhub.domains.classes.StudentGroup;
import com.example.eduhub.domains.classes.Subject;
import com.example.eduhub.domains.classes.Teacher;

import java.util.List;

public interface AssignmentService {
        Assignment createAssignment(Teacher teacher, Subject subject, StudentGroup group,
                                    String title, String description, Assignment.AssignmentType type);
        void updateAssignment(Assignment assignment);
        void deleteAssignment(Assignment assignment);
        HomeworkCompletion submitHomework(Student student, Assignment assignment, String comment);
        void gradeHomework(HomeworkCompletion completion, Integer points);
        List<Assignment> getGroupAssignments(StudentGroup group);
        List<HomeworkCompletion> getAssignmentCompletions(Assignment assignment);
}

