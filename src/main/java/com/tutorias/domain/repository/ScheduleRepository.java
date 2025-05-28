package com.tutorias.domain.repository;

import com.tutorias.domain.model.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {
    List<Schedule> getAll();
    Optional<Schedule> getById(int scheduleId);
    Schedule create(Schedule schedule);
}
