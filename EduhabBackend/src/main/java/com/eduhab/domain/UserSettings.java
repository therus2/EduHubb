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
@Table(name = "user_settings")
public class UserSettings {
    @Id
    @Column(name = "user_id")
    private int userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "notify_new_grades")
    @Builder.Default
    private Boolean notifyNewGrades = true;

    @Column(name = "notify_schedule_changes")
    @Builder.Default
    private Boolean notifyScheduleChanges = true;

    @Column(name = "notify_homework")
    @Builder.Default
    private Boolean notifyHomework = true;

    @Column(name = "notify_announcements")
    @Builder.Default
    private Boolean notifyAnnouncements = true;

    @Column(name = "theme")
    @Builder.Default
    private String theme = "light";

    @Column(name = "language")
    @Builder.Default
    private String language = "ru";
}
