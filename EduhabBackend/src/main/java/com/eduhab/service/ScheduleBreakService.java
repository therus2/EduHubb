package com.eduhab.service;

import com.eduhab.domain.ScheduleBreak;
import java.util.List;

public interface ScheduleBreakService {
    ScheduleBreak insert(ScheduleBreak scheduleBreak);
    ScheduleBreak getById(int id);
    List<ScheduleBreak> getAll();
    ScheduleBreak update(ScheduleBreak scheduleBreak);
    void deleteById(int id);
}
