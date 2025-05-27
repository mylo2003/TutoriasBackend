package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Salon;
import org.springframework.data.repository.CrudRepository;

public interface SalonCrudRepository extends CrudRepository<Salon, Integer> {
}
