package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Salon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalonCrudRepository extends JpaRepository<Salon, Integer> {
}
