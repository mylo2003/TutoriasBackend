package com.tutorias.persistance.crud;

import com.tutorias.domain.dto.ResponseScheduleDTO;
import com.tutorias.persistance.entity.Horario;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface HorarioCrudRepository extends JpaRepository<Horario, Integer> {
    List<Horario> findAllByIsDeletedFalseAndModoNot(String modo);
    List<ResponseScheduleDTO> findAllByUsuario_IdUsuarioAndIsDeletedFalseAndModoNot(Integer userId, String modo);

    // Profesor
    List<Horario> findByUsuario_IdUsuarioAndIsDeletedFalseAndFechaHorarioAfterOrderByFechaHorarioAsc(Integer idUsuario, LocalDateTime now);

    List<Horario> findTop15ByUsuario_IdUsuarioAndIsDeletedFalseAndFechaHorarioBeforeOrderByFechaHorarioDesc(Integer idUsuario, LocalDateTime now);

    // Estudiante
    @Query("""
        SELECT h FROM Horario h
        WHERE h.isDeleted = false
          AND h.materia.idMateria IN :materias
          AND h.fechaHorario > :now
          AND h.usuario.idUsuario != :idEstudiante
          AND h NOT IN (
            SELECT a.horario FROM Agendado a
             WHERE a.usuario.idUsuario = :idEstudiante
             AND a.eliminado = false
          )
        ORDER BY h.fechaHorario ASC
        """)
    List<Horario> findHorariosRelacionadosConMaterias(@Param("idEstudiante") Integer idEstudiante, @Param("materias") Set<Integer> materias, @Param("now") LocalDateTime now);

    @Query("""
        SELECT h FROM Horario h
        JOIN h.agendados a
        WHERE h.isDeleted = false
          AND a.usuario.idUsuario = :idEstudiante
          AND a.eliminado = false
          AND (h.modo IS NULL OR h.modo <> 'FINALIZADO')
        ORDER BY h.fechaHorario ASC
        """)
    List<Horario> findHorariosAgendadosPorEstudiante(@Param("idEstudiante") Integer idEstudiante);

    @Query("""
        SELECT h FROM Horario h
        JOIN h.agendados a
        WHERE h.isDeleted = false
          AND a.usuario.idUsuario = :idEstudiante
          AND h.fechaHorario < :now
          AND a.eliminado = false
        ORDER BY h.fechaHorario DESC
        """)
    List<Horario> findUltimas15FinalizadasDelEstudiante(@Param("idEstudiante") Integer idEstudiante, @Param("now") LocalDateTime now, Pageable pageable);

    default List<Horario> findUltimas15FinalizadasDelEstudiante(Integer id, LocalDateTime now) {
        return findUltimas15FinalizadasDelEstudiante(id, now, PageRequest.of(0, 15));
    }
}
