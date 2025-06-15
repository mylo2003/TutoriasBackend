package com.tutorias.domain.service;

import com.tutorias.config.AutomataEstado;
import com.tutorias.domain.dto.*;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    public Optional<ResponseScheduleEditDTO> getByIdToEdit(int scheduleId) {
        return scheduleRepository.getByIdToEdit(scheduleId);
    }

    public Map<String, List<ResponseScheduleFilterDTO>> obtenerHorariosPorUsuario(Integer idUsuario) {
        return scheduleRepository.obtenerHorariosPorUsuario(idUsuario);
    }

    public void createSchedule(CreateScheduleDTO schedule) {
        scheduleRepository.create(schedule);
    }

    public void deleteSchedule(int scheduleId) {
        scheduleRepository.delete(scheduleId);
    }

    public void updateSchedule(int scheduleId, CreateScheduleDTO schedule) {
        Schedule horarioAnterior = scheduleRepository.getById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        List<Booking> bookingsAfectados = bookingRepository.findBookingsByScheduleId(scheduleId);

        scheduleRepository.update(scheduleId, schedule);

        Schedule horarioActualizado = scheduleRepository.getById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        boolean isVirtual = "VIRTUAL".equals(horarioActualizado.getType());

        boolean cambioFecha = false;
        boolean cambioHoraInicio = false;
        boolean cambioHoraFin = false;
        boolean cambioSalon = false;

        cambioFecha = schedule.getScheduleDate() != null &&
                !horarioAnterior.getScheduleDate().equals(schedule.getScheduleDate());

        if (isVirtual) {
            cambioHoraInicio = schedule.getStartTime() != null &&
                    !horarioAnterior.getStartTime().equals(schedule.getStartTime());

            cambioHoraFin = schedule.getEndTime() != null &&
                    !horarioAnterior.getEndTime().equals(schedule.getEndTime());

            cambioSalon = false;
        } else {
            Availability nuevaDisponibilidad = null;
            if (schedule.getAvailabilityId() != 0) {
                nuevaDisponibilidad = availabilityRepository.getById(schedule.getAvailabilityId())
                        .orElseThrow(() -> new RuntimeException("Nueva disponibilidad no encontrada"));

                cambioHoraInicio = !horarioAnterior.getStartTime().equals(nuevaDisponibilidad.getStartTime());
                cambioHoraFin = !horarioAnterior.getEndTime().equals(nuevaDisponibilidad.getEndTime());
                cambioSalon = !horarioAnterior.getClassroomId().equals(nuevaDisponibilidad.getClassroomId());
            }
        }

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
        scheduleRepository.updateMode(scheduleId, mode);
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

        ResponseScheduleFilterDTO schedule = scheduleRepository.getByIdToMessage(horario.getScheduleId()).orElseThrow();

        StringBuilder mensaje = new StringBuilder("📢 Cambio de horario para tutoría agendada ")
                .append(schedule.getMateria().getNombreMateria())
                .append(" con el profe ")
                .append(schedule.getUsuario().getNombre()).append(" ")
                .append(schedule.getUsuario().getApellido()).append(". ");

        if (cambioFecha) {
            mensaje.append("Nueva fecha: ").append(horario.getScheduleDate()).append(". ");
        }

        if (cambioHoraInicio || cambioHoraFin) {
            mensaje.append("Nuevo horario: ").append(horario.getStartTime())
                    .append(" - ").append(horario.getEndTime()).append(". ");
        }

        if (cambioSalon) {
            String ubicacion;
            if (!"VIRTUAL".equals(schedule.getTipo())) {
                ubicacion = schedule.getSalon().getUbicacion() + " " +
                        (schedule.getDescripcion() != null ? schedule.getDescripcion() : "") + " - " +
                        schedule.getSalon().getBloque().getSeccion();
            } else {
                ubicacion = schedule.getSalon().getUbicacion();
            }
            mensaje.append("Nuevo salón: ").append(ubicacion).append(". ");
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
                    schedule.getScheduleDate(),
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
