package com.eduhab.service;

import com.eduhab.domain.Announcement;
import com.eduhab.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    @Override
    public List<Announcement> getAll() { return announcementRepository.findAll(); }

    @Override
    public Announcement insert(Announcement announcement) { return announcementRepository.save(announcement); }

    @Override
    public Announcement getById(int id) { return announcementRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Announcement not found: " + id)); }

    @Override
    public Announcement update(Announcement announcement) { return announcementRepository.save(announcement); }

    @Override
    public void deleteById(int id) { announcementRepository.deleteById(id); }

    @Override
    public List<Announcement> getPublished() { return announcementRepository.findByIsPublishedTrueOrderByCreatedAtDesc(); }

    @Override
    public List<Announcement> findByTargetGroupId(int targetGroupId) { return announcementRepository.findByTargetGroupId(targetGroupId); }

    @Override
    public List<Announcement> findByAuthorId(int authorId) { return announcementRepository.findByAuthorId(authorId); }
}
