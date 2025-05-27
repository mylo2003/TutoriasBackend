package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioCrudRepository extends CrudRepository<Usuario, Integer> {

}
