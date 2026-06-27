package com.example.eduhub.school.repository;

import com.example.eduhub.school.models.SchoolMenuItem;

import java.util.ArrayList;
import java.util.List;


public class SchoolRepository {

    
    public static List<SchoolMenuItem> getMenuItems() {
        List<SchoolMenuItem> items = new ArrayList<>();
        items.add(new SchoolMenuItem("Посещаемость", "Журнал визитов и опозданий"));
        items.add(new SchoolMenuItem("Объявления", "Приказы и важные новости"));
        return items;
    }
}
