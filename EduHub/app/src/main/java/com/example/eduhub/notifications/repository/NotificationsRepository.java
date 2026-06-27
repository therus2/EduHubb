package com.example.eduhub.notifications.repository;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.notifications.models.NotificationItem;

import java.util.ArrayList;
import java.util.List;


public class NotificationsRepository {

    private final DBHelper dbHelper;
    private final String userId;

    public NotificationsRepository(Context context, String userId) {
        this.dbHelper = DBHelper.getInstance(context);
        this.userId = userId;
    }

    private List<NotificationItem> buildItems(Cursor c) {
        List<NotificationItem> items = new ArrayList<>();
        while (c.moveToNext()) {
            String id      = c.getString(c.getColumnIndexOrThrow("id"));
            String type    = c.getString(c.getColumnIndexOrThrow("notification_type"));
            String title   = c.getString(c.getColumnIndexOrThrow("title"));
            String message = c.getString(c.getColumnIndexOrThrow("message"));
            int isReadInt  = c.getInt(c.getColumnIndexOrThrow("is_read"));
            String time    = formatTime(c.getString(c.getColumnIndexOrThrow("created_at")));
            String icon    = typeToIcon(type);
            String subtitle = message.length() > 40 ? message.substring(0, 40) + "..." : message;

            items.add(new NotificationItem(id, icon, title, subtitle, message, time, isReadInt == 1));
        }
        return items;
    }

    
    public List<NotificationItem> getUnreadNotifications() {
        List<NotificationItem> items = buildItems(dbHelper.findAllNotificationsForUserRaw(userId));
        List<NotificationItem> unread = new ArrayList<>();
        for (NotificationItem n : items) {
            if (!n.isRead()) unread.add(n);
        }
        if (unread.isEmpty()) {
            unread.add(new NotificationItem("", "ℹ", "Нет уведомлений", "Список пуст", "Новых уведомлений нет.", "Сейчас", false));
        }
        return unread;
    }

    
    public List<NotificationItem> getArchivedNotifications() {
        List<NotificationItem> items = buildItems(dbHelper.findAllNotificationsForUserRaw(userId));
        List<NotificationItem> read = new ArrayList<>();
        for (NotificationItem n : items) {
            if (n.isRead()) read.add(n);
        }
        if (read.isEmpty()) {
            read.add(new NotificationItem("", "ℹ", "Архив пуст", "Нет прочитанных уведомлений.", "", "Сейчас", true));
        }
        return read;
    }

    public void markAsRead(String notificationId) {
        dbHelper.markNotificationAsRead(notificationId);
    }

    private String typeToIcon(String type) {
        if (type == null) return "•";
        switch (type) {
            case "NEW_GRADE":        return "★";
            case "SCHEDULE_CHANGE":  return "□";
            case "NEW_HOMEWORK":
            case "HOMEWORK_DEADLINE":return "D";
            case "MESSAGE":          return "💬";
            case "ANNOUNCEMENT":     return "📢";
            default:                 return "•";
        }
    }

    private String formatTime(String datetime) {
        if (datetime == null) return "";
        
        try {
            return datetime.substring(11, 16); 
        } catch (Exception e) {
            return datetime;
        }
    }
}
