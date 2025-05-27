package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Materia;
import org.springframework.data.repository.CrudRepository;

public interface MateriaCrudRepository extends CrudRepository<Materia, Integer> {
}
