package com.tutorias.domain.repository;

import com.tutorias.domain.dto.CreateBookingDTO;
import com.tutorias.domain.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    List<Booking> getAll();
    Optional<Booking> getById(int bookingId);
    void create(CreateBookingDTO booking);
    void update(int bookingId, CreateBookingDTO booking);
    void delete(int bookingId);
    void rating(int bookingId, int rating);
}
