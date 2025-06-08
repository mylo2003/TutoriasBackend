package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolCrudRepository extends JpaRepository<Rol, Integer> {
}
