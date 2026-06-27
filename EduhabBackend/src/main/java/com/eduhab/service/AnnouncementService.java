package com.eduhab.service;

import com.eduhab.domain.Announcement;
import java.util.List;

public interface AnnouncementService {
    Announcement insert(Announcement announcement);
    Announcement getById(int id);
    List<Announcement> getAll();
    Announcement update(Announcement announcement);
    void deleteById(int id);
    List<Announcement> getPublished();
    List<Announcement> findByTargetGroupId(int targetGroupId);
    List<Announcement> findByAuthorId(int authorId);
}
