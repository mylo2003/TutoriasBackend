package com.tutorias.domain.repository;

import com.tutorias.domain.dto.CreateScheduleDTO;
import com.tutorias.domain.model.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {
    List<Schedule> getAll();
    Optional<Schedule> getById(int scheduleId);
    void create(CreateScheduleDTO schedule);
    void update(int scheduleId, CreateScheduleDTO schedule);
    void delete(int scheduleId);
    void updateMode(int scheduleId, String mode);
}
