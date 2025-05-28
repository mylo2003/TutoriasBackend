package com.tutorias.domain.service;

import com.tutorias.domain.dto.CreateScheduleDTO;
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

    public void createSchedule(CreateScheduleDTO schedule) {
        scheduleRepository.create(schedule);
    }

    public void updateSchedule(int scheduleId, CreateScheduleDTO schedule) {
        scheduleRepository.update(scheduleId, schedule);
    }

    public void deleteSchedule(int scheduleId) {
        scheduleRepository.delete(scheduleId);
    }
}
