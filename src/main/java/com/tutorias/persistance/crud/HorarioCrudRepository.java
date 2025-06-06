package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioCrudRepository extends JpaRepository<Horario, Integer> {
    List<Horario> findAllByIsDeletedFalseAndModoNot(String modo);
    List<Horario> findAllByUsuario_IdUsuarioAndIsDeletedFalseAndModoNot(Integer userId, String modo);
}
