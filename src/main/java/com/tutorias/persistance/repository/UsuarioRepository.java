package com.tutorias.persistance.repository;

import com.tutorias.domain.dto.CreateUserDTO;
import com.tutorias.domain.dto.EditSubjectUserDTO;
import com.tutorias.domain.model.User;
import com.tutorias.domain.repository.UserRepository;
import com.tutorias.persistance.crud.MateriaUsuarioCrudRepository;
import com.tutorias.persistance.crud.UsuarioCrudRepository;
import com.tutorias.persistance.crud.*;
import com.tutorias.persistance.entity.Usuario;
import com.tutorias.persistance.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository implements UserRepository{
    @Autowired
    private UsuarioCrudRepository usuarioCrudRepository;
    @Autowired
    private MateriaUsuarioCrudRepository materiaUsuarioCrudRepository;
    @Autowired
    private UserMapper mapper;
    @Autowired
    private HorarioCrudRepository horarioCrudRepository;

    @Override
    public List<User> getAll(){ return mapper.toUsers(usuarioCrudRepository.findAll()); }

    @Override
    public Optional<User> getById(Integer idUser){
        return usuarioCrudRepository.findById(idUser).map(mapper::toUser);
    }

    @Override
    public void updateAverage(int tutorId, double average) {
        Usuario tutor = usuarioCrudRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));

        tutor.setValoracionPromedio(average);
    }

    @Override
    public void updateUser(int idUser, CreateUserDTO body, String encryptedPassword){
        Usuario usuario = usuarioCrudRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (body.getRoleID() != null){
            usuario.setIdRol(body.getRoleID());
        }
        if (body.getCareerID() != null){
            usuario.setIdCarrera(body.getCareerID());
        }
        if (body.getName() != null && !body.getName().trim().isEmpty()){
            usuario.setNombre(body.getName().trim());
        }
        if (body.getLastName() != null && !body.getLastName().trim().isEmpty()){
            usuario.setApellido(body.getLastName().trim());
        }
        if (body.getUser() != null && !body.getUser().trim().isEmpty()){
            usuario.setUsuario(body.getUser().trim());
        }
        if (body.getEmail() != null && !body.getEmail().trim().isEmpty()){
            usuario.setCorreo(body.getEmail().trim().toLowerCase());
        }
        if (body.getSemester() != null){
            usuario.setSemestre(body.getSemester());
        }
        if (encryptedPassword != null){
            usuario.setContrasenia(encryptedPassword);
        }

        usuarioCrudRepository.save(usuario);
    }

    @Override
    public List<Integer> getSubjectsByIdUser(Integer idUser){
        return materiaUsuarioCrudRepository.findMateriaIdsByUsuarioId(idUser);
    }

    @Override
    public void deleteUserSubjects(EditSubjectUserDTO body) {
        materiaUsuarioCrudRepository.deleteByIdUsuarioAndIdMateriaIn(body.getIdUser(), body.getIdSubjects());
    }

    @Override
    public void deleteUser(Integer idUser) {
        usuarioCrudRepository.deleteById(idUser);
    }

    @Override
    public boolean existsByIdUser(Integer idUser){
        return usuarioCrudRepository.existsById(idUser);
    }

    @Override
    public boolean existsByUsernameAndNotId(String username, Integer idUser){
        return usuarioCrudRepository.existsByUsuarioAndIdUsuarioNot(username, idUser);
    }

    @Override
    public boolean existsByEmailAndNotId(String email, Integer idUser){
        return usuarioCrudRepository.existsByCorreoAndIdUsuarioNot(email, idUser);
    }

    @Override
    public List<User> findProfesoresByMateriaIds(List<Integer> subjectIds, Integer rolId) {
        return mapper.toUsers(usuarioCrudRepository.findProfesoresByMateriaIds(subjectIds, rolId));
    }

    @Override
    public Integer countSchedulesByIdUser(int idUser){
        return horarioCrudRepository.countByUsuario_IdUsuario(idUser);
    }
}
