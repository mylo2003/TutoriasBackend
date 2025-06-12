package com.tutorias.persistance.crud;

import com.tutorias.domain.dto.ResponseScheduleDTO;
import com.tutorias.persistance.entity.Horario;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface HorarioCrudRepository extends JpaRepository<Horario, Integer> {
    List<Horario> findAllByIsDeletedFalseAndModoNot(String modo);
    List<ResponseScheduleDTO> findAllByUsuario_IdUsuarioAndIsDeletedFalseAndModoNot(Integer userId, String modo);

    // Horarios activos del profesor (DISPONIBLE o EN_CURSO)
    List<Horario> findByUsuario_IdUsuarioAndIsDeletedFalseAndModoInOrderByFechaHorarioAsc(
            Integer idUsuario, List<String> modos);

    // Últimos 15 horarios finalizados del profesor
    List<Horario> findTop15ByUsuario_IdUsuarioAndIsDeletedFalseAndModoOrderByFechaHorarioDesc(
            Integer idUsuario, String modo);

    // Estudiante
    @Query("""
    SELECT h FROM Horario h
    WHERE h.isDeleted = false
      AND h.materia.idMateria IN :materias
      AND h.modo IN ('DISPONIBLE', 'EN_CURSO')
      AND h.usuario.idUsuario != :idEstudiante
      AND h.tipo IN ('VIRTUAL', 'PRESENCIAL')
      AND h NOT IN (
        SELECT a.horario FROM Agendado a
         WHERE a.usuario.idUsuario = :idEstudiante
         AND a.eliminado = false
      )
    ORDER BY h.fechaHorario ASC
    """)
    List<Horario> findHorariosRelacionadosConMaterias(@Param("idEstudiante") Integer idEstudiante, @Param("materias") Set<Integer> materias);

    // Horarios agendados por el estudiante (DISPONIBLE o EN_CURSO)
    @Query("""
    SELECT h FROM Horario h
    JOIN h.agendados a
    WHERE h.isDeleted = false
      AND a.usuario.idUsuario = :idEstudiante
      AND a.eliminado = false
      AND h.modo IN ('DISPONIBLE', 'EN_CURSO')
      AND h.tipo IN ('VIRTUAL', 'PRESENCIAL')
    ORDER BY h.fechaHorario ASC
    """)
    List<Horario> findHorariosAgendadosPorEstudiante(@Param("idEstudiante") Integer idEstudiante);

    // Últimas 15 tutorías finalizadas del estudiante
    @Query("""
    SELECT h FROM Horario h
    JOIN h.agendados a
    WHERE h.isDeleted = false
      AND a.usuario.idUsuario = :idEstudiante
      AND a.eliminado = false
      AND h.modo = 'FINALIZADO'
      AND h.tipo IN ('VIRTUAL', 'PRESENCIAL')
    ORDER BY h.fechaHorario DESC
    """)
    List<Horario> findUltimas15FinalizadasDelEstudiante(@Param("idEstudiante") Integer idEstudiante, Pageable pageable);

    default List<Horario> findUltimas15FinalizadasDelEstudiante(Integer id) {
        return findUltimas15FinalizadasDelEstudiante(id, PageRequest.of(0, 15));
    }

    @Query("SELECT h FROM Horario h WHERE h.salon.id = :salonId AND h.fechaHorario = :fecha AND h.isDeleted = false")
    List<Horario> findBySalon_IdSalonAndFechaHorario(@Param("salonId") Integer salonId,
                                                     @Param("fecha") LocalDate fecha);
}
