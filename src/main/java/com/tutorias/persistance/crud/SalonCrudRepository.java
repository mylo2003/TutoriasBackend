package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Salon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalonCrudRepository extends JpaRepository<Salon, Integer> {
    Page<Salon> findAllByBloque_IdBloque(Integer blockId, Pageable pageable);
    Integer countByBloque_IdBloque(Integer idBloque);
}
