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
@Table(name = "homework_completions", uniqueConstraints = @UniqueConstraint(columnNames = {"assignment_id", "student_id"}))
public class HomeworkCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "student_comment")
    private String studentComment;

    @Column(name = "attachments")
    private String attachments;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "received_points")
    private Integer receivedPoints;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
}
