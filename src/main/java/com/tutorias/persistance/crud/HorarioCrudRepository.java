package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioCrudRepository extends JpaRepository<Horario, Integer> {
}
