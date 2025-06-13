package com.tutorias.persistance.crud;

import com.tutorias.persistance.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioCrudRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsuario(String usuario);
    boolean existsByUsuario(String usuario);
    boolean existsByCorreo(String correo);
    boolean existsByUsuarioAndIdUsuarioNot(String newUsername, Integer idUser);
    boolean existsByCorreoAndIdUsuarioNot(String newEmail, Integer idUser);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.usuario = :username")
    Optional<Usuario> findByUsuarioWithRol(@Param("username") String username);

    @Query("SELECT DISTINCT mu.usuario FROM MateriaUsuario mu " +
            "WHERE mu.materia.id IN :materiasIds AND mu.usuario.idRol = :idRolProfesor")
    List<Usuario> findProfesoresByMateriaIds(@Param("materiasIds") List<Integer> materiasIds,
                                             @Param("idRolProfesor") Integer idRolProfesor);
}
