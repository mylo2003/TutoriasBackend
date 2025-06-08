package com.tutorias.domain.repository;

import com.tutorias.domain.dto.CreateScheduleDTO;
import com.tutorias.domain.dto.ResponseScheduleDTO;
import com.tutorias.domain.model.Schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {
    List<Schedule> getAll();
    Optional<Schedule> getById(int scheduleId);
    List<Schedule> filterSchedule(Integer subjectId, Integer classroomId, LocalDate date, String mode, String dayOfWeek);
    void create(CreateScheduleDTO schedule);
    void update(int scheduleId, CreateScheduleDTO schedule);
    void delete(int scheduleId);
    void updateMode(int scheduleId, String mode);
    List<Schedule> findAllByIsDeletedFalse();
    List<ResponseScheduleDTO> getAllByUserId(int userId);
}
