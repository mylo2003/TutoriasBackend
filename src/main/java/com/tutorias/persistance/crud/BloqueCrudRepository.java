package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Bloque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BloqueCrudRepository extends JpaRepository<Bloque, Integer> {
    Optional<Bloque> findByNombreBloque(String nombreBloque);
}
