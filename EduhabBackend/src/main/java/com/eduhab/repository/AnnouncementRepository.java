package com.eduhab.repository;

import com.eduhab.domain.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    List<Announcement> findByIsPublishedTrueOrderByCreatedAtDesc();
    List<Announcement> findByTargetGroupId(int groupId);
    List<Announcement> findByAuthorId(int authorId);
}
