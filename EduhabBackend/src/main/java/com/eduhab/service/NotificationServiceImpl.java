package com.eduhab.service;

import com.eduhab.domain.Notification;
import com.eduhab.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public List<Notification> getAll() { return notificationRepository.findAll(); }

    @Override
    public Notification insert(Notification notification) { return notificationRepository.save(notification); }

    @Override
    public Notification getById(int id) { return notificationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Notification not found: " + id)); }

    @Override
    public Notification update(Notification notification) { return notificationRepository.save(notification); }

    @Override
    public void deleteById(int id) { notificationRepository.deleteById(id); }

    @Override
    public List<Notification> getByUserId(int userId) { return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId); }

    @Override
    public List<Notification> getByUserIdAndIsRead(int userId, Boolean isRead) {
        if (isRead == null) {
            return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        return notificationRepository.findByUserIdAndIsRead(userId, isRead);
    }
}
