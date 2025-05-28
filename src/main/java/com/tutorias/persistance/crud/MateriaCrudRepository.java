package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MateriaCrudRepository extends JpaRepository<Materia, Integer> {
    Optional<Materia> findByNombreMateria(String nombreMateria);
}
