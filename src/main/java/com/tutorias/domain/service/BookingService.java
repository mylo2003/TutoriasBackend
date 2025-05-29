package com.tutorias.domain.service;

import com.tutorias.domain.dto.CreateBookingDTO;
import com.tutorias.domain.model.Booking;
import com.tutorias.domain.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public List<Booking> getAll() {
        return bookingRepository.getAll();
    }

    public Optional<Booking> getById(int bookingId) {
        return bookingRepository.getById(bookingId);
    }

    public void createBooking(CreateBookingDTO booking) {
        bookingRepository.create(booking);
    }

    public void updateBooking(int bookingId, CreateBookingDTO booking) {
        bookingRepository.update(bookingId, booking);
    }

    public void deleteBooking(int bookingId) {
        bookingRepository.delete(bookingId);
    }

    public void updateRating(int bookingId, int rating) {
        bookingRepository.rating(bookingId, rating);
    }
}
