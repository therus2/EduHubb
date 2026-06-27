package com.eduhab.repository;

import com.eduhab.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(int userId);
    List<Notification> findByUserIdAndIsReadFalse(int userId);
    List<Notification> findByUserIdAndIsRead(int userId, Boolean isRead);
}
