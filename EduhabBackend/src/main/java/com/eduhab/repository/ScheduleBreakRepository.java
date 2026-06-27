package com.eduhab.repository;

import com.eduhab.domain.ScheduleBreak;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScheduleBreakRepository extends JpaRepository<ScheduleBreak, Integer> {
    List<ScheduleBreak> findByGroupId(int groupId);
    List<ScheduleBreak> findByGroupIdAndDayOfWeek(int groupId, int dayOfWeek);
}
