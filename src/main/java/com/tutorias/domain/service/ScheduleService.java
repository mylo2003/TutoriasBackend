package com.tutorias.domain.service;

import com.tutorias.domain.dto.CreateScheduleDTO;
import com.tutorias.domain.model.Schedule;
import com.tutorias.domain.repository.ScheduleRepository;
import com.tutorias.persistance.crud.HorarioCrudRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private HorarioCrudRepository horarioCrudRepository;

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

    public void updateMode(int scheduleId, String mode) {
        scheduleRepository.updateMode(scheduleId, mode);
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void actualizarModosDeHorarios() {
        List<Schedule> schedules = scheduleRepository.findAllByIsDeletedFalse();

        LocalDateTime ahora = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDate hoy = ahora.toLocalDate();
        LocalTime horaActual = ahora.toLocalTime();

        for (Schedule schedule : schedules) {
            LocalDate fechaSchedule = schedule.getScheduleDate().toLocalDate();
            LocalTime horaInicio = schedule.getStartTime();
            LocalTime horaFin = schedule.getEndTime();

            String modoActual = schedule.getMode();
            String nuevoModo = determinarModoCorrect(fechaSchedule, horaInicio, horaFin, hoy, horaActual);

            if (!nuevoModo.equals(modoActual) && !"FINALIZADO".equals(modoActual)) {
                scheduleRepository.updateMode(schedule.getScheduleId(), nuevoModo);

                System.out.println("Schedule ID " + schedule.getScheduleId() +
                        " actualizado de '" + modoActual + "' a '" + nuevoModo + "'");
            }
        }
    }

    private String determinarModoCorrect(LocalDate fechaSchedule, LocalTime horaInicio, LocalTime horaFin,
                                         LocalDate fechaActual, LocalTime horaActual) {

        if (fechaSchedule.isBefore(fechaActual)) {
            return "FINALIZADO";
        }

        if (fechaSchedule.isAfter(fechaActual)) {
            return "DISPONIBLE";
        }

        if (fechaSchedule.equals(fechaActual)) {
            if (horaActual.isBefore(horaInicio)) {
                return "DISPONIBLE";
            }

            if (!horaActual.isBefore(horaInicio) && horaActual.isBefore(horaFin)) {
                return "EN CURSO";
            }

            if (!horaActual.isBefore(horaFin)) {
                return "FINALIZADO";
            }
        }

        return "DISPONIBLE";
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void actualizarModosDeHorariosCompacto() {
        List<Schedule> schedules = scheduleRepository.findAllByIsDeletedFalse();
        LocalDateTime ahora = LocalDateTime.now().withSecond(0).withNano(0);

        schedules.stream()
                .filter(schedule -> !"FINALIZADO".equals(schedule.getMode())) // No procesar ya finalizados
                .forEach(schedule -> {
                    LocalDateTime inicio = LocalDateTime.of(
                            schedule.getScheduleDate().toLocalDate(),
                            schedule.getStartTime()
                    );
                    LocalDateTime fin = LocalDateTime.of(
                            schedule.getScheduleDate().toLocalDate(),
                            schedule.getEndTime()
                    );

                    String nuevoModo;
                    if (ahora.isBefore(inicio)) {
                        nuevoModo = "DISPONIBLE";
                    } else if (ahora.isBefore(fin)) {
                        nuevoModo = "EN CURSO";
                    } else {
                        nuevoModo = "FINALIZADO";
                    }

                    // Solo actualizar si es diferente
                    if (!nuevoModo.equals(schedule.getMode())) {
                        scheduleRepository.updateMode(schedule.getScheduleId(), nuevoModo);
                    }
                });
    }

    public void actualizarModoSchedule(int scheduleId) {
        Schedule schedule = scheduleRepository.getById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule no encontrado con ID: " + scheduleId));

        LocalDateTime ahora = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDate hoy = ahora.toLocalDate();
        LocalTime horaActual = ahora.toLocalTime();

        LocalDate fechaSchedule = schedule.getScheduleDate().toLocalDate();
        String nuevoModo = determinarModoCorrect(
                fechaSchedule,
                schedule.getStartTime(),
                schedule.getEndTime(),
                hoy,
                horaActual
        );

        if (!nuevoModo.equals(schedule.getMode())) {
            scheduleRepository.updateMode(scheduleId, nuevoModo);
        }
    }

    public Map<String, Long> obtenerEstadisticasModos() {
        List<Schedule> schedules = scheduleRepository.findAllByIsDeletedFalse();

        return schedules.stream()
                .collect(Collectors.groupingBy(
                        schedule -> schedule.getMode() != null ? schedule.getMode() : "SIN_ESTADO",
                        Collectors.counting()
                ));
    }
}
