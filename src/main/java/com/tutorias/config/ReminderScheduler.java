package com.tutorias.config;

import com.tutorias.domain.model.Booking;
import com.tutorias.domain.repository.BookingRepository;
import com.tutorias.domain.service.SseService;
import com.tutorias.persistance.crud.AgendacionCrudRepository;
import com.tutorias.persistance.entity.Agendado;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private SseService sseService;

    @Scheduled(fixedRate = 60000)
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
                String mensaje = "📢 Recordatorio: tu tutoría inicia en " + minutosAntes + " minutos.";
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
            String mensaje = "✅ Tu tutoría ha comenzado. ¡Buena suerte!";
            System.out.println("Notificando inicio a usuario " + userId);
            sseService.sendEvent(userId, mensaje);
        }
    }
}


