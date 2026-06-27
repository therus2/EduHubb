package com.eduhab.service;

import com.eduhab.domain.ScheduleBreak;
import com.eduhab.repository.ScheduleBreakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleBreakServiceImpl implements ScheduleBreakService {
    private final ScheduleBreakRepository scheduleBreakRepository;

    @Override
    public List<ScheduleBreak> getAll() { return scheduleBreakRepository.findAll(); }

    @Override
    public ScheduleBreak insert(ScheduleBreak scheduleBreak) { return scheduleBreakRepository.save(scheduleBreak); }

    @Override
    public ScheduleBreak getById(int id) { return scheduleBreakRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("ScheduleBreak not found: " + id)); }

    @Override
    public ScheduleBreak update(ScheduleBreak scheduleBreak) { return scheduleBreakRepository.save(scheduleBreak); }

    @Override
    public void deleteById(int id) { scheduleBreakRepository.deleteById(id); }
}
