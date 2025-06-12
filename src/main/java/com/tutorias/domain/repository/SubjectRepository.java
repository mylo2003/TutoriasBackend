package com.tutorias.domain.repository;

import com.tutorias.domain.dto.CreateSubjectDTO;
import com.tutorias.domain.dto.SubjectFilter;
import com.tutorias.domain.model.Subject;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository {
    List<Subject> getAll();
    Optional<Subject> getById(int subjectId);
    Page<Subject> filterSubjects(String subjectName, Integer careerId, String careerName, int page, int elements);
    List<SubjectFilter> filterByCareerId(int careerId);
    void create(CreateSubjectDTO subject);
    void delete(int subjectId);
    List<Integer> getMateriaIdsExisting(List<Integer> idSubjects);
}
