package com.tutorias.domain.repository;

import com.tutorias.domain.dto.CreateSubjectDTO;
import com.tutorias.domain.model.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository {
    List<Subject> getAll();
    Optional<Subject> getById(int subjectId);
    void create(CreateSubjectDTO subject);
}
