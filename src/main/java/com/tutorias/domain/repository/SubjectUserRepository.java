package com.tutorias.domain.repository;

import com.tutorias.domain.dto.SubjectFilter;
import com.tutorias.domain.model.SubjectUser;

import java.util.List;

public interface SubjectUserRepository {
    List<SubjectUser> findByUsuarioIdUsuario(Integer idEstudiante);
    List<SubjectFilter> filterByUserId(Integer userId);
    List<Integer> getMateriaIdsByUsuarioId(Integer idUser);
}
