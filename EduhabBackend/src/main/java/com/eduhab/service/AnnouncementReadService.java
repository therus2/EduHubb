package com.eduhab.service;

import com.eduhab.domain.AnnouncementRead;
import java.util.List;

public interface AnnouncementReadService {
    AnnouncementRead insert(AnnouncementRead announcementRead);
    AnnouncementRead getById(int id);
    List<AnnouncementRead> getAll();
    AnnouncementRead update(AnnouncementRead announcementRead);
    void deleteById(int id);
}
