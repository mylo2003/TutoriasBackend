package com.tutorias.domain.service;

import com.tutorias.config.AutomataEstado;
import com.tutorias.domain.dto.CreateScheduleDTO;
import com.tutorias.domain.dto.EstadoAsesoria;
import com.tutorias.domain.dto.ResponseScheduleDTO;
import com.tutorias.domain.dto.ResponseScheduleFilterDTO;
import com.tutorias.domain.model.Availability;
import com.tutorias.domain.model.Booking;
import com.tutorias.domain.model.Schedule;
import com.tutorias.domain.repository.AvailabilityRepository;
import com.tutorias.domain.repository.BookingRepository;
import com.tutorias.domain.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
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
    @Autowired
    private SseService sseService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private AvailabilityRepository availabilityRepository;

    public List<Schedule> getAll() {
        return scheduleRepository.getAll();
    }

    public Optional<Schedule> getById(int scheduleId) {
        return scheduleRepository.getById(scheduleId);
    }

    public List<ResponseScheduleFilterDTO> filterSchedule(List<Integer> subjectId, Integer classroomId, LocalDate date, String mode, String dayOfWeek) {
        return scheduleRepository.filterSchedule(subjectId, classroomId, date, mode, dayOfWeek);
    }

    public void createSchedule(CreateScheduleDTO schedule) {
        scheduleRepository.create(schedule);
    }

    public void deleteSchedule(int scheduleId) {
        updateOccupied(scheduleId);
        scheduleRepository.delete(scheduleId);
    }

    public void updateSchedule(int scheduleId, CreateScheduleDTO schedule) {
        Schedule horarioAnterior = scheduleRepository.getById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        if ("PRESENCIAL".equals(horarioAnterior.getType())) {
            updateOccupied(horarioAnterior.getScheduleId());
        }

        List<Booking> bookingsAfectados = bookingRepository.findBookingsByScheduleId(scheduleId);

        scheduleRepository.update(scheduleId, schedule);

        Schedule horarioActualizado = scheduleRepository.getById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        Availability nuevaDisponibilidad = null;
        if (schedule.getAvailabilityId() != 0) {
            nuevaDisponibilidad = availabilityRepository.getById(schedule.getAvailabilityId())
                    .orElseThrow(() -> new RuntimeException("Nueva disponibilidad no encontrada"));
        }

        boolean cambioFecha = schedule.getScheduleDate() != null &&
                !horarioAnterior.getScheduleDate().equals(schedule.getScheduleDate());

        boolean cambioHoraInicio = nuevaDisponibilidad != null &&
                !horarioAnterior.getStartTime().equals(nuevaDisponibilidad.getStartTime());

        boolean cambioHoraFin = nuevaDisponibilidad != null &&
                !horarioAnterior.getEndTime().equals(nuevaDisponibilidad.getEndTime());

        boolean cambioSalon = nuevaDisponibilidad != null &&
                !horarioAnterior.getClassroomId().equals(nuevaDisponibilidad.getClassroomId());

        if ((cambioFecha || cambioHoraInicio || cambioHoraFin || cambioSalon) && !bookingsAfectados.isEmpty()) {
            String mensajeNotificacion = construirMensajeNotificacion(
                    horarioActualizado, cambioFecha, cambioHoraInicio, cambioHoraFin, cambioSalon
            );

            for (Booking booking : bookingsAfectados) {
                Integer userId = booking.getUserId();
                System.out.println("Notificando cambio de horario a usuario " + userId + ": " + mensajeNotificacion);
                sseService.sendEvent(userId, mensajeNotificacion);
            }
        }
    }

    public void updateMode(int scheduleId, String mode) {
        updateOccupied(scheduleId);
        scheduleRepository.updateMode(scheduleId, mode);
    }

    private void updateOccupied(int scheduleId) {
        Schedule horarioAnterior = scheduleRepository.getById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        if (!"PRESENCIAL".equalsIgnoreCase(horarioAnterior.getType())) {
            return;
        }

        String diaSemanaEsp = traducirDiaADiaEspanol(horarioAnterior.getScheduleDate().getDayOfWeek());

        List<Availability> disponibilidadesAnteriores = availabilityRepository.filterAvailability(
                horarioAnterior.getClassroomId(),
                diaSemanaEsp,
                horarioAnterior.getStartTime(),
                horarioAnterior.getEndTime()
        );

        if (disponibilidadesAnteriores.isEmpty()) {
            System.out.println("No se encontró disponibilidad para el horario PRESENCIAL con ID: " + scheduleId);
            return;
        }

        availabilityRepository.updateOccupied(disponibilidadesAnteriores.getFirst().getAvailabilityId());
    }

    private String traducirDiaADiaEspanol(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "LUNES";
            case TUESDAY -> "MARTES";
            case WEDNESDAY -> "MIERCOLES";
            case THURSDAY -> "JUEVES";
            case FRIDAY -> "VIERNES";
            case SATURDAY -> "SABADO";
            case SUNDAY -> "DOMINGO";
        };
    }

    private String construirMensajeNotificacion(Schedule horario, boolean cambioFecha,
                                                boolean cambioHoraInicio, boolean cambioHoraFin,
                                                boolean cambioSalon) {
        StringBuilder mensaje = new StringBuilder("📢 Cambio en tu tutoría agendada: ");

        if (cambioFecha) {
            mensaje.append("Nueva fecha: ").append(horario.getScheduleDate()).append(". ");
        }

        if (cambioHoraInicio || cambioHoraFin) {
            mensaje.append("Nuevo horario: ").append(horario.getStartTime())
                    .append(" - ").append(horario.getEndTime()).append(". ");
        }

        if (cambioSalon) {
            mensaje.append("Nuevo salón: ").append(horario.getClassroomId());
        }

        mensaje.append("Revisa los detalles en la aplicación.");
        return mensaje.toString();
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
                updateMode(schedule.getScheduleId(), nuevoModo.name());
                System.out.println("Modo actualizado a: " + nuevoModo.name());

                if (nuevoModo == EstadoAsesoria.FINALIZADO) {
                    ratingService.calcularPromedioConRetraso(schedule);
                }
            }
        }
    }

    public List<ResponseScheduleDTO> getAllByUserId(int userId) {
        return scheduleRepository.getAllByUserId(userId);
    }
}
