package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.MateriaUsuario;
import com.tutorias.persistance.entity.MateriaUsuarioPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MateriaUsuarioCrudRepository extends JpaRepository<MateriaUsuario, MateriaUsuarioPK> {
    List<MateriaUsuario> findByUsuario_IdUsuario(Integer idUsuario);
}
