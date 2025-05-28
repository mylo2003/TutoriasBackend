package com.tutorias.persistance.repository;

import com.tutorias.domain.dto.CreateSubjectDTO;
import com.tutorias.domain.model.Subject;
import com.tutorias.domain.repository.SubjectRepository;
import com.tutorias.persistance.crud.CarreraCrudRepository;
import com.tutorias.persistance.crud.MateriaCrudRepository;
import com.tutorias.persistance.entity.Carrera;
import com.tutorias.persistance.entity.Materia;
import com.tutorias.persistance.mapper.SubjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MateriaRepository implements SubjectRepository {
    @Autowired
    private MateriaCrudRepository jpaRepository;
    @Autowired
    private CarreraCrudRepository carreraCrudRepository;
    @Autowired
    private SubjectMapper mapper;

    @Override
    public List<Subject> getAll() {
        List<Materia> materias = jpaRepository.findAll();
        return mapper.toSubjects(materias);
    }

    @Override
    public Optional<Subject> getById(int subjectId) {
        return jpaRepository.findById(subjectId)
                .map(mapper::toSubject);
    }

    @Override
    public void create(CreateSubjectDTO subject) {
        boolean yaExiste = jpaRepository.findByNombreMateria(subject.getSubjectName()).isPresent();

        if (yaExiste) {
            throw new RuntimeException("La materia ya se encuentra registrada");
        }

        Carrera carrera = carreraCrudRepository.findById(subject.getCareerId())
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada"));

        Materia materia = new Materia();

        materia.setIdCarrera(subject.getCareerId());
        materia.setCarrera(carrera);
        materia.setNombreMateria(subject.getSubjectName());
        jpaRepository.save(materia);
    }

    @Override
    public void delete(int subjectId) {
        Materia materia = jpaRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        materia.setIsDeleted(true);
        jpaRepository.save(materia);
    }
}
