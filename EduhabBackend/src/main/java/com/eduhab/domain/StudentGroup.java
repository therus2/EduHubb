package com.eduhab.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "student_groups")
public class StudentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "course_number", nullable = false)
    private Integer courseNumber;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "max_students")
    @Builder.Default
    private Integer maxStudents = 30;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
