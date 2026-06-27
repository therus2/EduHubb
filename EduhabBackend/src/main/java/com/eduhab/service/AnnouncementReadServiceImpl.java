package com.eduhab.service;

import com.eduhab.domain.AnnouncementRead;
import com.eduhab.repository.AnnouncementReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AnnouncementReadServiceImpl implements AnnouncementReadService {
    private final AnnouncementReadRepository announcementReadRepository;

    @Override
    public List<AnnouncementRead> getAll() { return announcementReadRepository.findAll(); }

    @Override
    public AnnouncementRead insert(AnnouncementRead announcementRead) { return announcementReadRepository.save(announcementRead); }

    @Override
    public AnnouncementRead getById(int id) { return announcementReadRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("AnnouncementRead not found: " + id)); }

    @Override
    public AnnouncementRead update(AnnouncementRead announcementRead) { return announcementReadRepository.save(announcementRead); }

    @Override
    public void deleteById(int id) { announcementReadRepository.deleteById(id); }
}
