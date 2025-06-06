package com.tutorias.persistance.repository;

import com.tutorias.domain.dto.CreateBookingDTO;
import com.tutorias.domain.model.Booking;
import com.tutorias.domain.repository.BookingRepository;
import com.tutorias.persistance.crud.*;
import com.tutorias.persistance.entity.*;
import com.tutorias.persistance.mapper.BookingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Repository
public class AgendacionRepository implements BookingRepository {
    @Autowired
    private AgendacionCrudRepository jpaRepository;
    @Autowired
    private UsuarioCrudRepository usuarioCrudRepository;
    @Autowired
    private HorarioCrudRepository horarioCrudRepository;
    @Autowired
    private BookingMapper mapper;

    @Override
    public List<Booking> getAll() {
        List<Agendado> agendados = jpaRepository.findAll();
        return mapper.toBookings(agendados);
    }

    @Override
    public Optional<Booking> getById(int bookingId) {
        return jpaRepository.findById(bookingId)
                .map(mapper::toBooking);
    }

    @Override
    public void create(CreateBookingDTO booking) {
        Usuario usuario = usuarioCrudRepository.findById(booking.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Horario horario = horarioCrudRepository.findById(booking.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        Agendado agendado = new Agendado();

        agendado.setUsuario(usuario);
        agendado.setHorario(horario);
        agendado.setHoraAgendado(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        jpaRepository.save(agendado);
    }

    @Override
    public void update(int bookingId, CreateBookingDTO booking) {
        Agendado agendado = jpaRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Agendación no encontrada"));

        if (booking.getUserId() != 0) {
            Usuario usuario = usuarioCrudRepository.findById(booking.getUserId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            agendado.setUsuario(usuario);
        }

        if (booking.getScheduleId() != 0) {
            Horario horario = horarioCrudRepository.findById(booking.getScheduleId())
                    .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
            agendado.setHorario(horario);
        }

        agendado.setHoraAgendado(LocalDateTime.now());

        jpaRepository.save(agendado);
    }

    @Override
    public void delete(int bookingId) {
        Agendado agendado = jpaRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Agendación no encontrada"));

        agendado.setEliminado(true);
        agendado.setDeletedAt(LocalDateTime.now());
        jpaRepository.save(agendado);
    }

    @Override
    public void rating(int bookingId, int calificacion) {
        Agendado agendado = jpaRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Agendación no encontrada"));

        agendado.setCalificacion(calificacion);
        agendado.setNotificado(true);
        jpaRepository.save(agendado);
    }

    @Override
    public List<Booking> findBookingsByHoraCercana(LocalDate fecha, LocalTime horaMenos, LocalTime horaMas) {
        return mapper.toBookings(jpaRepository.findBookingsByHoraCercana(fecha, horaMenos, horaMas));
    }

    @Override
    public List<Booking> findBookingsQueFinalizaron(LocalDate fecha, LocalTime finMenos, LocalTime finMas) {
        return mapper.toBookings(jpaRepository.findBookingsQueFinalizaron(fecha, finMenos, finMas));
    }

    @Override
    public List<Booking> findRatings(int userId) {
        return mapper.toBookings(jpaRepository.findByHorario_Usuario_IdUsuarioAndCalificacionIsNotNull(userId));
    }

    @Override
    public void notifiedRating(int bookingId) {
        Agendado agendado = jpaRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Agendación no encontrada"));

        agendado.setNotificado(true);
        jpaRepository.save(agendado);
    }

    @Override
    public List<Booking> findBookingsByScheduleId(int scheduleId) {
        return mapper.toBookings(jpaRepository.findAgendadosByHorarioId(scheduleId));
    }

    @Override
    public List<Booking> getAllByUserId(int userId) {
        return mapper.toBookings(jpaRepository.findAllByUsuario_IdUsuarioAndHorario_ModoNotAndEliminadoFalse(userId, "FINALIZADO"));
    }
}
