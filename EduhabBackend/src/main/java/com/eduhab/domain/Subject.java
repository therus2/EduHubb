package com.eduhab.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "total_hours")
    @Builder.Default
    private Integer totalHours = 0;

    @Column(name = "credits")
    @Builder.Default
    private Integer credits = 0;

    @Column(name = "is_exam")
    @Builder.Default
    private Boolean isExam = false;

    @Column(name = "is_credit")
    @Builder.Default
    private Boolean isCredit = false;
}
