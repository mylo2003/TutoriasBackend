package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.MateriaUsuario;
import com.tutorias.persistance.entity.MateriaUsuarioPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MateriaUsuarioCrudRepository extends JpaRepository<MateriaUsuario, MateriaUsuarioPK> {
    List<MateriaUsuario> findByUsuario_IdUsuario(Integer idUsuario);

    @Query("SELECT mu FROM MateriaUsuario mu " +
            "JOIN FETCH mu.materia m " +
            "JOIN FETCH m.carrera c " +
            "WHERE mu.usuario.idUsuario = :idUsuario " +
            "AND m.isDeleted = false")
    List<MateriaUsuario> findByUsuario_IdUsuarioWithMateriaAndCarrera(@Param("idUsuario") Integer idUsuario);

    @Modifying
    @Query("DELETE FROM MateriaUsuario mu WHERE mu.usuario.idUsuario = :idUsuario")
    void deleteByUsuario_IdUsuario(@Param("idUsuario") Integer idUsuario);

    boolean existsByUsuario_IdUsuarioAndMateria_IdMateria(Integer idUsuario, Integer idMateria);

    @Query("SELECT COUNT(mu) FROM MateriaUsuario mu WHERE mu.usuario.idUsuario = :idUsuario")
    Long countByUsuario_IdUsuario(@Param("idUsuario") Integer idUsuario);

    List<MateriaUsuario> findByMateria_IdMateria(Integer idMateria);
}
