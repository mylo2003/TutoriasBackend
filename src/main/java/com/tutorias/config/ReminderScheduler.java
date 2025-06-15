package com.tutorias.config;

import com.tutorias.domain.dto.ResponseScheduleFilterDTO;
import com.tutorias.domain.model.Booking;
import com.tutorias.domain.model.User;
import com.tutorias.domain.repository.BookingRepository;
import com.tutorias.domain.repository.ScheduleRepository;
import com.tutorias.domain.repository.UserRepository;
import com.tutorias.domain.service.SseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SseService sseService;

    @Async("notificacionExecutor")
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void notificarTutoriasProximas() {
        System.out.println("Ejecutando scheduler de recordatorios");

        LocalDate fecha = LocalDate.now();
        LocalTime ahora = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

        List<Integer> offsets = List.of(30, 15, 5);

        // Notificaciones 30, 15 y 5 minutos antes
        for (Integer minutosAntes : offsets) {
            LocalTime horaObjetivo = ahora.plusMinutes(minutosAntes);
            LocalTime horaMenos = horaObjetivo.minusSeconds(30);
            LocalTime horaMas = horaObjetivo.plusSeconds(30);

            List<Booking> bookings = bookingRepository.findBookingsByHoraCercana(
                    fecha, horaMenos, horaMas
            );

            for (Booking ag : bookings) {
                Integer userId = ag.getUserId();
                String mensaje = construirMensajes(ag.getScheduleId(), userId, minutosAntes, 1);
                System.out.println("Preparando notificación para usuario " + userId + ": " + mensaje);
                sseService.sendEvent(userId, mensaje);
            }
        }

        // Notificación justo cuando empieza la tutoría
        LocalTime inicioMenos = ahora.minusSeconds(30);
        LocalTime inicioMas = ahora.plusSeconds(30);

        List<Booking> agendadosQueEmpiezan = bookingRepository.findBookingsByHoraCercana(
                fecha, inicioMenos, inicioMas
        );

        for (Booking ag : agendadosQueEmpiezan) {
            Integer userId = ag.getUserId();
            String mensaje = construirMensajes(ag.getScheduleId(), userId, 0, 2);
            System.out.println("Notificando inicio a usuario " + userId);
            sseService.sendEvent(userId, mensaje);
        }

        // Notificar a estudiantes que su tutoría finalizó
        LocalTime finMenos = ahora.minusSeconds(30);
        LocalTime finMas = ahora.plusSeconds(30);

        List<Booking> tutoriasFinalizadas = bookingRepository.findBookingsQueFinalizaron(
                fecha, finMenos, finMas
        );

        for (Booking ag : tutoriasFinalizadas) {
            Integer userId = ag.getUserId();
            String mensaje = construirMensajes(ag.getScheduleId(), userId, 0, 3);
            System.out.println("Notificando finalización a usuario " + userId);
            sseService.sendEvent(userId, mensaje);

            System.out.println("llamando a modificar notificacion a true");
            bookingRepository.notifiedRating(ag.getBookingId());
        }
    }

    private String construirMensajes(Integer scheduleId, Integer userId, Integer minutosAntes, Integer tipo) {
        ResponseScheduleFilterDTO schedule = scheduleRepository.getByIdToMessage(scheduleId).orElseThrow();
        Optional<User> user = userRepository.getById(userId);
        String salon = null;
        String descripcion = schedule.getDescripcion() != null ? schedule.getDescripcion() : "";
        if (!"VIRTUAL".equals(schedule.getTipo())) {
            salon = schedule.getSalon().getUbicacion() + " " + descripcion  + " - " + schedule.getSalon().getBloque().getSeccion();
        } else {
            salon = schedule.getSalon().getUbicacion();
        }
        String estudiante = user.get().getName();
        String profesor = schedule.getUsuario().getNombre() + " " + schedule.getUsuario().getApellido();
        String materia = schedule.getMateria().getNombreMateria();
        String horario = schedule.getHoraFin() + " - " + schedule.getHoraFin();

        String mensaje = null;

        switch (tipo) {
            case 1:
                if (minutosAntes >= 30) {
                    mensaje = MessageFormat.format("{0}, tu tutoría para {1} con el profesor {2} en el salón {3} con horario {4} inicia en {5} minutos. Ve preparándote 📖", estudiante, materia, profesor, salon, horario, minutosAntes);

                } else if (minutosAntes > 5) {
                    mensaje = MessageFormat.format("Tu tutoría para {0} en el salón {1} con horario {2} inicia en {3} minutos. ¿Estás en camino?  🚶🏻‍➡️️", materia, salon, horario, minutosAntes);

                } else {
                    mensaje = MessageFormat.format("Tu tutoría para {0} inicia en {1} minutos! En el salón {2} con el profe {3}! Si todavía no has llegado, es momento de correr!. ⏱️", materia, minutosAntes, salon, profesor);
                }
                break;
            case 2:
                mensaje = MessageFormat.format("{0}, tu tutoría para {1} ha iniciado! En el salón {2} con el profe {3}. Excelente aprendizaje!. 🎉", estudiante, materia, salon, profesor);
                break;
            case 3:
                mensaje = MessageFormat.format("Tu tutoría sobre {0} ha finalizado! Es momento para agendar una nueva!. 🖊️", materia);
                break;
            default:
                mensaje = MessageFormat.format("Cómo va todo {0}! Esperamos que te encuentres genial 🌟", estudiante);

        }

        return mensaje;
    }
}


