package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Agendado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AgendacionCrudRepository extends JpaRepository<Agendado, Integer> {
    @Query("SELECT a FROM Agendado a " +
            "WHERE a.finalizado = false " +
            "AND DATE(a.horario.fechaHorario) = :fecha " +
            "AND a.horario.horaInicio BETWEEN :horaMenos AND :horaMas")
    List<Agendado> findBookingsByHoraCercana(@Param("fecha") LocalDate fecha,
                                             @Param("horaMenos") LocalTime horaMenos,
                                             @Param("horaMas") LocalTime horaMas);
}
