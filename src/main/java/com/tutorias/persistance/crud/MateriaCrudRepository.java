package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaCrudRepository extends JpaRepository<Materia, Integer> {
}
