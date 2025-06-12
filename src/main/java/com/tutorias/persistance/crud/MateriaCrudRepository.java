package com.tutorias.persistance.crud;

import com.tutorias.domain.dto.SubjectFilter;
import com.tutorias.persistance.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MateriaCrudRepository extends JpaRepository<Materia, Integer> {
    Optional<Materia> findByNombreMateria(String nombreMateria);
    List<Materia> findByIdMateriaInAndIsDeletedFalse(List<Integer> ids);

    @Query("SELECT m.idMateria AS idMateria, m.nombreMateria AS nombreMateria " +
            "FROM Materia m " +
            "WHERE m.idCarrera = :idCarrera AND m.isDeleted = false")
    List<SubjectFilter> findByCarreraId(Integer idCarrera);

    @Query("SELECT m.idMateria FROM Materia m WHERE m.idMateria IN :ids AND m.isDeleted = false")
    List<Integer> findExistingMateriaIds(@Param("ids") List<Integer> ids);

    boolean existsByIdMateriaAndIsDeletedFalse(Integer idMateria);

}
