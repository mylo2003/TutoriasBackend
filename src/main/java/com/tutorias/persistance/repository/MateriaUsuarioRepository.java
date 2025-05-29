package com.tutorias.persistance.repository;

import com.tutorias.domain.dto.ModifySubjectUserDTO;
import com.tutorias.domain.dto.SubjectUserResponseDTO;
import com.tutorias.domain.repository.SubjectUserRepository;
import com.tutorias.persistance.crud.MateriaCrudRepository;
import com.tutorias.persistance.crud.MateriaUsuarioCrudRepository;
import com.tutorias.persistance.crud.UsuarioCrudRepository;
import com.tutorias.persistance.entity.Materia;
import com.tutorias.persistance.entity.MateriaUsuario;
import com.tutorias.persistance.entity.MateriaUsuarioPK;
import com.tutorias.persistance.entity.Usuario;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class MateriaUsuarioRepository implements SubjectUserRepository {
    @Autowired
    private MateriaUsuarioCrudRepository jpaRepository;
    @Autowired
    private UsuarioCrudRepository usuarioCrudRepository;
    @Autowired
    private MateriaCrudRepository materiaCrudRepository;

    public void addSubjects(ModifySubjectUserDTO request) {
        Usuario usuario = usuarioCrudRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        List<Materia> materias = materiaCrudRepository.findAllById(request.getSubjectIds());
        if (materias.size() != request.getSubjectIds().size()) {
            throw new IllegalArgumentException("Una o más materias no existen");
        }

        List<MateriaUsuario> materiasExistentes = jpaRepository
                .findByUsuario_IdUsuario(request.getUserId());

        Set<Integer> materiasExistentesIds = materiasExistentes.stream()
                .map(mu -> mu.getMateria().getIdMateria())
                .collect(Collectors.toSet());

        List<MateriaUsuario> nuevasAsociaciones = new ArrayList<>();
        for (Materia materia : materias) {
            if (!materiasExistentesIds.contains(materia.getIdMateria())) {
                MateriaUsuarioPK pk = new MateriaUsuarioPK(
                        usuario.getIdUsuario(),
                        materia.getIdMateria()
                );

                MateriaUsuario materiaUsuario = new MateriaUsuario();
                materiaUsuario.setId(pk);
                materiaUsuario.setUsuario(usuario);
                materiaUsuario.setMateria(materia);

                nuevasAsociaciones.add(materiaUsuario);
            }
        }

        if (!nuevasAsociaciones.isEmpty()) {
            jpaRepository.saveAll(nuevasAsociaciones);
        }
    }

    public void updateSubjects(ModifySubjectUserDTO request) {
        Usuario usuario = usuarioCrudRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        jpaRepository.deleteByUsuario_IdUsuario(request.getUserId());

        if (request.getSubjectIds().isEmpty()) {
            return;
        }

        List<Materia> materias = materiaCrudRepository.findAllById(request.getSubjectIds());
        if (materias.size() != request.getSubjectIds().size()) {
            throw new IllegalArgumentException("Una o más materias no existen");
        }

        List<MateriaUsuario> nuevasAsociaciones = materias.stream()
                .map(materia -> {
                    MateriaUsuarioPK pk = new MateriaUsuarioPK(
                            usuario.getIdUsuario(),
                            materia.getIdMateria()
                    );

                    MateriaUsuario materiaUsuario = new MateriaUsuario();
                    materiaUsuario.setId(pk);
                    materiaUsuario.setUsuario(usuario);
                    materiaUsuario.setMateria(materia);

                    return materiaUsuario;
                })
                .collect(Collectors.toList());

        jpaRepository.saveAll(nuevasAsociaciones);
    }

    public void deleteSubjects(ModifySubjectUserDTO request) {
        usuarioCrudRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        for (Integer idMateria : request.getSubjectIds()) {
            MateriaUsuarioPK pk = new MateriaUsuarioPK(request.getUserId(), idMateria);
            jpaRepository.deleteById(pk);
        }
    }

    @Transactional
    public SubjectUserResponseDTO getSubjects(Integer idUsuario) {
        Usuario usuario = usuarioCrudRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        List<MateriaUsuario> materiasUsuario = jpaRepository
                .findByUsuario_IdUsuarioWithMateriaAndCarrera(idUsuario);

        List<SubjectUserResponseDTO.SubjectInfoDTO> materias = materiasUsuario.stream()
                .map(mu -> new SubjectUserResponseDTO.SubjectInfoDTO(
                        mu.getMateria().getIdMateria(),
                        mu.getMateria().getNombreMateria(),
                        mu.getMateria().getCarrera().getNombreCarrera()
                ))
                .collect(Collectors.toList());

        return new SubjectUserResponseDTO(
                usuario.getIdUsuario(),
                usuario.getNombre() + " " + usuario.getApellido(),
                materias
        );
    }
}
