package com.tutorias.web.controller;

import com.tutorias.domain.dto.CreateBookingDTO;
import com.tutorias.domain.model.Booking;
import com.tutorias.domain.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/agendados")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<Booking>> getBookings() {
        try {
            List<Booking> bookingList = bookingService.getAll();

            if (bookingList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(bookingList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{idAgendacion}")
    public ResponseEntity<?> getBooking(@PathVariable int idAgendacion) {
        try {
            Optional<Booking> booking = bookingService.getById(idAgendacion);
            if (booking.isPresent()) {
                return ResponseEntity.ok(booking.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Agendación no encontrada"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody CreateBookingDTO booking) {
        try {
            bookingService.createBooking(booking);
            return ResponseEntity.status(HttpStatus.CREATED).body("Agendación creada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @PutMapping("/{idAgendacion}")
    public ResponseEntity<?> updateBooking(@PathVariable int idAgendacion, @RequestBody CreateBookingDTO booking) {
        try {
            bookingService.updateBooking(idAgendacion, booking);
            return ResponseEntity.status(HttpStatus.OK).body("Agendación actualizada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @DeleteMapping("/{idAgendacion}")
    public ResponseEntity<?> deleteBooking(@PathVariable int idAgendacion) {
        try {
            bookingService.deleteBooking(idAgendacion);
            return ResponseEntity.status(HttpStatus.OK).body("Agendación eliminada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}
