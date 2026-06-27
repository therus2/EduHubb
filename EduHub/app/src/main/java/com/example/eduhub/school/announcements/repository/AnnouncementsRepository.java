package com.example.eduhub.school.announcements.repository;

import android.content.Context;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.domains.classes.Announcement;
import com.example.eduhub.school.announcements.models.AnnouncementItem;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementsRepository {
    private DBHelper dbHelper;
    private String groupId;
    private String studentId;

    public AnnouncementsRepository(Context context, String groupId, String studentId) {
        this.dbHelper = DBHelper.getInstance(context);
        this.groupId = groupId;
        this.studentId = studentId;
    }

    public List<AnnouncementItem> getUnreadAnnouncements() {
        List<AnnouncementItem> items = buildItems();
        List<AnnouncementItem> unread = new ArrayList<>();
        for (AnnouncementItem item : items) {
            if (!item.isAcknowledged()) unread.add(item);
        }
        if (unread.isEmpty()) {
            unread.add(new AnnouncementItem("", "ИНФО", "Новых объявлений нет", "Список пуст", "Нет доступных объявлений.", "Сейчас", false, false));
        }
        return unread;
    }

    public List<AnnouncementItem> getArchivedAnnouncements() {
        List<AnnouncementItem> items = buildItems();
        List<AnnouncementItem> read = new ArrayList<>();
        for (AnnouncementItem item : items) {
            if (item.isAcknowledged()) read.add(item);
        }
        if (read.isEmpty()) {
            read.add(new AnnouncementItem("", "ИНФО", "Архив пуст", "Нет прочитанных объявлений.", "", "Сейчас", false, true));
        }
        return read;
    }

    private List<AnnouncementItem> buildItems() {
        List<AnnouncementItem> items = new ArrayList<>();
        try {
            List<Announcement> dbAnnouncements = groupId != null && !groupId.isEmpty()
                ? dbHelper.findActiveAnnouncementsByGroupId(groupId)
                : dbHelper.findAllAnnouncements();
            for (Announcement ann : dbAnnouncements) {
                String timeStr = "Недавно";
                try {
                    if (ann.getCreatedAt() != null) {
                        timeStr = ann.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                    }
                } catch (Exception ignored) {}

                String annId = ann.getId().toString();
                boolean isRead = studentId != null && !studentId.isEmpty()
                        && dbHelper.isAnnouncementRead(annId, studentId);

                String content = ann.getContent() != null ? ann.getContent() : "";
                String desc = content.length() > 30 ? content.substring(0, 30) + "..." : content;
                String title = ann.getTitle() != null ? ann.getTitle() : "";
                items.add(new AnnouncementItem(
                    annId, "ОБЪЯВЛЕНИЕ", title, desc, content, timeStr, !isRead, isRead));
            }
        } catch (Exception ignored) {}
        return items;
    }

    public void markAsRead(String announcementId) {
        if (announcementId == null || announcementId.isEmpty()) return;
        dbHelper.markAnnouncementAsRead(announcementId, studentId);
    }
}