package com.eduhab.repository;

import com.eduhab.domain.AnnouncementRead;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnnouncementReadRepository extends JpaRepository<AnnouncementRead, Integer> {
    List<AnnouncementRead> findByStudentId(int studentId);
    List<AnnouncementRead> findByAnnouncementId(int announcementId);
}
