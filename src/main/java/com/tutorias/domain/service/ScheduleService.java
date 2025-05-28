package com.tutorias.domain.service;

import com.tutorias.domain.model.Schedule;
import com.tutorias.domain.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Schedule> getAll() {
        return scheduleRepository.getAll();
    }

    public Optional<Schedule> getById(int scheduleId) {
        return scheduleRepository.getById(scheduleId);
    }

    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.create(schedule);
    }
}
