package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Disponibilidad;
import com.tutorias.persistance.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DisponibilidadCrudRepository extends JpaRepository<Disponibilidad, Integer> {
}
