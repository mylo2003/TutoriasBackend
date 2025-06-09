package com.tutorias.persistance.repository;

import com.tutorias.domain.model.SubjectUser;
import com.tutorias.domain.repository.SubjectUserRepository;
import com.tutorias.persistance.crud.MateriaUsuarioCrudRepository;
import com.tutorias.persistance.mapper.SubjectUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MateriaUsuarioRepository implements SubjectUserRepository {
    @Autowired
    private MateriaUsuarioCrudRepository jpaRepository;
    @Autowired
    private SubjectUserMapper mapper;

    @Override
    public List<SubjectUser> findByUsuarioIdUsuario(Integer idEstudiante) {
        return mapper.toSubjectUsers(jpaRepository.findByUsuario_IdUsuario(idEstudiante));
    }
}
