package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Disponibilidad;
import com.tutorias.persistance.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DisponibilidadCrudRepository extends JpaRepository<Disponibilidad, Integer> {
    @Query("SELECT d FROM Disponibilidad d WHERE d.salon.id = :salonId AND d.diaSemana = :diaSemana")
    List<Disponibilidad> findBySalonIdAndDiaSemana(@Param("salonId") Integer salonId,
                                                   @Param("diaSemana") String diaSemana);
}
