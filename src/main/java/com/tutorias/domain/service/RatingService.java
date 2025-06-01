package com.tutorias.domain.service;

import com.tutorias.domain.model.Booking;
import com.tutorias.domain.model.Schedule;
import com.tutorias.domain.repository.BookingRepository;
import com.tutorias.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Async("estadoExecutor")
    public void calcularPromedioConRetraso(Schedule schedule) {
        try {
            // Esperar 5 minutos (300,000 ms)
            Thread.sleep(5 * 60 * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        calcularValoracionPromedio(schedule);
    }

    @Transactional
    public void calcularValoracionPromedio(Schedule schedule) {
        int idTutor = schedule.getUserId();

        List<Booking> valoraciones = bookingRepository.findRatings(idTutor);

        if (valoraciones.isEmpty()) return;

        double promedio = valoraciones.stream()
                .mapToInt(Booking::getRating)
                .average()
                .orElse(0.0);

        userRepository.updateAverage(idTutor, promedio);
    }
}
