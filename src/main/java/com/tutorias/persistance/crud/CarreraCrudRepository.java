package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarreraCrudRepository extends JpaRepository<Carrera, Integer> {
    Optional<Carrera> findByNombreCarrera(String nombreCarrera);
    Optional<Carrera> findByCodigo(Integer codigo);
}
