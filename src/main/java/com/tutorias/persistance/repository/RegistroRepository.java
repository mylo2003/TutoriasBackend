package com.tutorias.persistance.repository;

import com.tutorias.domain.dto.RegisterDTO;
import com.tutorias.domain.dto.RegisterResponseDTO;
import com.tutorias.domain.repository.RegisterRepository;
import com.tutorias.persistance.crud.MateriaCrudRepository;
import com.tutorias.persistance.crud.MateriaUsuarioCrudRepository;
import com.tutorias.persistance.crud.UsuarioCrudRepository;
import com.tutorias.persistance.entity.Materia;
import com.tutorias.persistance.entity.MateriaUsuario;
import com.tutorias.persistance.entity.MateriaUsuarioPK;
import com.tutorias.persistance.entity.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RegistroRepository implements RegisterRepository {
    @Autowired
    private UsuarioCrudRepository usuarioCrudRepository;

    @Autowired
    private MateriaUsuarioCrudRepository materiaUsuarioCrudRepository;

    @Autowired
    private MateriaCrudRepository materiaCrudRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponseDTO registerUser(RegisterDTO registerDTO) {
        // 1. Validar que el usuario no exista
        if (usuarioCrudRepository.existsByUsuario(registerDTO.getUser())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // 2. Validar que el correo no exista
        if (usuarioCrudRepository.existsByCorreo(registerDTO.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }

        // 3. Validar que las materias existan
        List<Materia> materiasExistentes = materiaCrudRepository.findByIdMateriaInAndIsDeletedFalse(registerDTO.getSubjects());
        if (materiasExistentes.size() != registerDTO.getSubjects().size()) {
            throw new RuntimeException("Una o más materias seleccionadas no existen o están inactivas");
        }

        // 4. Crear el usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setIdRol(registerDTO.getRoleID());
        nuevoUsuario.setIdCarrera(1);
        nuevoUsuario.setNombre(registerDTO.getName());
        nuevoUsuario.setApellido(registerDTO.getLastName());
        nuevoUsuario.setUsuario(registerDTO.getUser());
        nuevoUsuario.setContrasenia(passwordEncoder.encode(registerDTO.getPassword())); // Encriptar contraseña
        nuevoUsuario.setCorreo(registerDTO.getEmail());
        nuevoUsuario.setSemestre(registerDTO.getSemester());
        nuevoUsuario.setValoracionPromedio(0.0);
        nuevoUsuario.setIsDeleted(false);

        // 5. Guardar el usuario
        Usuario usuarioGuardado = usuarioCrudRepository.save(nuevoUsuario);

        // 6. Crear las relaciones con las materias
        List<MateriaUsuario> materiasUsuario = new ArrayList<>();
        for (Integer idMateria : registerDTO.getSubjects()) {
            MateriaUsuarioPK pk = new MateriaUsuarioPK();
            pk.setIdUsuario(usuarioGuardado.getIdUsuario());
            pk.setIdMateria(idMateria);

            MateriaUsuario materiaUsuario = new MateriaUsuario();
            materiaUsuario.setId(pk);
            materiaUsuario.setUsuario(usuarioGuardado);

            // Buscar la materia para establecer la relación
            Materia materia = materiasExistentes.stream()
                    .filter(m -> m.getIdMateria().equals(idMateria))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Materia no encontrada: " + idMateria));
            materiaUsuario.setMateria(materia);

            materiasUsuario.add(materiaUsuario);
        }

        // 7. Guardar las relaciones
        materiaUsuarioCrudRepository.saveAll(materiasUsuario);

        // 8. Retornar respuesta exitosa
        return new RegisterResponseDTO(
                "Usuario registrado exitosamente",
                usuarioGuardado.getIdUsuario(),
                usuarioGuardado.getUsuario()
        );
    }
}
