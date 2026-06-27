package com.eduhab.service;

import com.eduhab.domain.Notification;
import java.util.List;

public interface NotificationService {
    Notification insert(Notification notification);
    Notification getById(int id);
    List<Notification> getAll();
    Notification update(Notification notification);
    void deleteById(int id);
    List<Notification> getByUserId(int userId);
    List<Notification> getByUserIdAndIsRead(int userId, Boolean isRead);
}
