package com.tutorias.domain.service;

import com.tutorias.domain.dto.CreateSubjectDTO;
import com.tutorias.domain.dto.SubjectFilter;
import com.tutorias.domain.model.Subject;
import com.tutorias.domain.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;

    public List<Subject> getAll() {
        return subjectRepository.getAll();
    }

    public List<SubjectFilter> filterByCareerId(int careerId) {
        return subjectRepository.filterByCareerId(careerId);
    }

    public Optional<Subject> getById(int subjectId) {
        return subjectRepository.getById(subjectId);
    }

    public Page<Subject> filterSubjects(String subjectName, Integer careerId, String careerName, int page, int elements) {
        return subjectRepository.filterSubjects(subjectName, careerId, careerName, page, elements);
    }

    public void createSubject(CreateSubjectDTO subject) {
        subjectRepository.create(subject);
    }

    public void deleteSubject(int subjectId) {
        subjectRepository.delete(subjectId);
    }
}
