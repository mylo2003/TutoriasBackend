package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Agendado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendacionCrudRepository extends JpaRepository<Agendado, Integer> {
}
