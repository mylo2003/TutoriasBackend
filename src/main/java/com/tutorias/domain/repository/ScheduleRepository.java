package com.tutorias.domain.repository;

import com.tutorias.domain.dto.CreateScheduleDTO;
import com.tutorias.domain.dto.ResponseScheduleDTO;
import com.tutorias.domain.dto.ResponseScheduleEditDTO;
import com.tutorias.domain.dto.ResponseScheduleFilterDTO;
import com.tutorias.domain.model.Schedule;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ScheduleRepository {
    List<Schedule> getAll();
    Optional<Schedule> getById(int scheduleId);
    Optional<ResponseScheduleEditDTO> getByIdToEdit(int scheduleId);
    Optional<ResponseScheduleFilterDTO> getByIdToMessage(int scheduleId);
    Map<String, List<ResponseScheduleFilterDTO>> obtenerHorariosPorUsuario(Integer idUsuario);
    void create(CreateScheduleDTO schedule);
    void update(int scheduleId, CreateScheduleDTO schedule);
    void delete(int scheduleId);
    void updateMode(int scheduleId, String mode);
    List<Schedule> findAllByIsDeletedFalse();
    List<ResponseScheduleDTO> getAllByUserId(int userId);
}
