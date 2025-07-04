package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Agendado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AgendacionCrudRepository extends JpaRepository<Agendado, Integer> {
    @Query("SELECT a FROM Agendado a " +
            "WHERE a.eliminado = false " +
            "AND DATE(a.horario.fechaHorario) = :fecha " +
            "AND a.horario.horaInicio BETWEEN :horaMenos AND :horaMas")
    List<Agendado> findBookingsByHoraCercana(@Param("fecha") LocalDate fecha,
                                             @Param("horaMenos") LocalTime horaMenos,
                                             @Param("horaMas") LocalTime horaMas);

    Optional<Agendado> findByUsuario_IdUsuarioAndHorario_IdHorarioAndEliminadoTrue(Integer idUsuario, Integer idHorario);

    @Query("""
    SELECT b FROM Agendado b
    WHERE FUNCTION('DATE', b.horario.fechaHorario) = :fecha
      AND b.horario.horaFin BETWEEN :inicio AND :fin
      AND b.eliminado = false
      AND b.notificado = false
""")
    List<Agendado> findBookingsQueFinalizaron(
            @Param("fecha") LocalDate fecha,
            @Param("inicio") LocalTime inicio,
            @Param("fin") LocalTime fin
    );

    List<Agendado> findByHorario_Usuario_IdUsuarioAndCalificacionIsNotNull(Integer idUsuario);

    @Query("SELECT a FROM Agendado a WHERE a.horario.id = :horarioId AND a.eliminado = false")
    List<Agendado> findAgendadosByHorarioId(@Param("horarioId") Integer horarioId);

    List<Agendado> findAllByUsuario_IdUsuarioAndHorario_ModoNotAndEliminadoFalse(Integer userId, String modo);
}
