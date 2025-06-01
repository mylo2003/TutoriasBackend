package com.tutorias.domain.repository;

import com.tutorias.domain.dto.CreateBookingDTO;
import com.tutorias.domain.model.Booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    List<Booking> getAll();
    Optional<Booking> getById(int bookingId);
    void create(CreateBookingDTO booking);
    void update(int bookingId, CreateBookingDTO booking);
    void delete(int bookingId);
    void rating(int bookingId, int rating);
    List<Booking> findBookingsByHoraCercana(LocalDate fecha, LocalTime horaMenos, LocalTime horaMas);
    List<Booking> findBookingsQueFinalizaron(LocalDate fecha, LocalTime finMenos, LocalTime finMas);
    List<Booking> findRatings(int userId);
    void notifiedRating(int bookingId);
}
