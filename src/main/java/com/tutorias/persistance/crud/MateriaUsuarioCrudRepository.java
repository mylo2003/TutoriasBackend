package com.tutorias.persistance.crud;

import com.tutorias.domain.dto.SubjectFilter;
import com.tutorias.persistance.entity.MateriaUsuario;
import com.tutorias.persistance.entity.MateriaUsuarioPK;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MateriaUsuarioCrudRepository extends JpaRepository<MateriaUsuario, MateriaUsuarioPK> {
    List<MateriaUsuario> findByUsuario_IdUsuario(Integer idUsuario);

    @Modifying
    @Transactional
    @Query("DELETE FROM MateriaUsuario mu WHERE mu.id.idUsuario = :idUsuario")
    void deleteByIdUsuario(@Param("idUsuario") Integer idUsuario);

    @Modifying
    @Transactional
    @Query("DELETE FROM MateriaUsuario mu WHERE mu.id.idUsuario = :idUsuario AND mu.id.idMateria IN :idMaterias")
    void deleteByIdUsuarioAndIdMateriaIn(@Param("idUsuario") Integer idUsuario, @Param("idMaterias") List<Integer> idMaterias);

    @Query("SELECT mu.id.idMateria FROM MateriaUsuario mu WHERE mu.id.idUsuario = :idUsuario")
    List<Integer> findMateriaIdsByUsuarioId(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT m.materia.idMateria AS idMateria, m.materia.nombreMateria AS nombreMateria " +
            "FROM MateriaUsuario m " +
            "WHERE m.usuario.idUsuario = :idUsuario")
    List<SubjectFilter> findMateriasByUsuario(@Param("idUsuario") Integer idUsuario);
}
