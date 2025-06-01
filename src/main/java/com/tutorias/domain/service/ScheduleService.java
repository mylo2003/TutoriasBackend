package com.tutorias.domain.service;

import com.tutorias.config.AutomataEstado;
import com.tutorias.domain.dto.CreateScheduleDTO;
import com.tutorias.domain.dto.EstadoAsesoria;
import com.tutorias.domain.model.Schedule;
import com.tutorias.domain.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private AutomataEstado automataEstado;

    public List<Schedule> getAll() {
        return scheduleRepository.getAll();
    }

    public Optional<Schedule> getById(int scheduleId) {
        return scheduleRepository.getById(scheduleId);
    }

    public Page<Schedule> filterSchedule(Integer subjectId, Integer classroomId, LocalDate date, String mode, String dayOfWeek, int page, int elements) {
        return scheduleRepository.filterSchedule(subjectId, classroomId, date, mode, dayOfWeek, page, elements);
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

    @Async("estadoExecutor")
    @Transactional
    @Scheduled(fixedRate = 60000)
    public void actualizarModosDeHorarios() {
        List<Schedule> schedules = scheduleRepository.findAllByIsDeletedFalse();
        LocalDateTime ahora = LocalDateTime.now().withSecond(0).withNano(0);

        for (Schedule schedule : schedules) {
            if ("FINALIZADO".equals(schedule.getMode())) continue;

            EstadoAsesoria nuevoModo = automataEstado.transitar(
                    ahora,
                    schedule.getScheduleDate().toLocalDate(),
                    schedule.getStartTime(),
                    schedule.getEndTime()
            );

            if (!nuevoModo.name().equals(schedule.getMode())) {
                scheduleRepository.updateMode(schedule.getScheduleId(), nuevoModo.name());
                System.out.println("Modo actualizado a: " + nuevoModo.name());

                if (nuevoModo == EstadoAsesoria.FINALIZADO) {
                    ratingService.calcularPromedioConRetraso(schedule);
                }
            }
        }
    }
}
