package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioCrudRepository extends JpaRepository<Usuario, Integer> {

}
