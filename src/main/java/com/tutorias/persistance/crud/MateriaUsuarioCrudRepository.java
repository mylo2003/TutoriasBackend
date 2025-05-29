package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.MateriaUsuario;
import com.tutorias.persistance.entity.MateriaUsuarioPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaUsuarioCrudRepository extends JpaRepository<MateriaUsuario, MateriaUsuarioPK> {
}
