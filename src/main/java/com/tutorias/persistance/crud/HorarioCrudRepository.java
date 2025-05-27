package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Horario;
import org.springframework.data.repository.CrudRepository;

public interface HorarioCrudRepository extends CrudRepository<Horario, Integer> {
}
