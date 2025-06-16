package com.tutorias.persistance.repository;

import com.tutorias.domain.dto.CreateSubjectDTO;
import com.tutorias.domain.dto.SubjectFilter;
import com.tutorias.domain.model.Career;
import com.tutorias.domain.model.Subject;
import com.tutorias.domain.repository.SubjectRepository;
import com.tutorias.persistance.crud.CarreraCrudRepository;
import com.tutorias.persistance.crud.MateriaCrudRepository;
import com.tutorias.persistance.entity.Carrera;
import com.tutorias.persistance.entity.Materia;
import com.tutorias.persistance.mapper.SubjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class MateriaRepository implements SubjectRepository {
    @Autowired
    private MateriaCrudRepository jpaRepository;
    @Autowired
    private CarreraCrudRepository carreraCrudRepository;
    @Autowired
    private SubjectMapper mapper;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Subject> getAll() {
        List<Materia> materias = jpaRepository.findByIsDeletedFalse();
        return mapper.toSubjects(materias);
    }

    @Override
    public Optional<Subject> getById(int subjectId) {
        return jpaRepository.findByIdMateriaAndIsDeletedFalse(subjectId)
                .map(mapper::toSubject);
    }

    @Override
    public Page<Subject> filterSubjects(String subjectName, Integer careerId, String careerName, int page, int elements) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Materia> countRoot = countQuery.from(Materia.class);
        List<Predicate> countPredicates = buildSubjectPredicates(cb, countRoot, subjectName, careerId, careerName);
        countQuery.select(cb.countDistinct(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        CriteriaQuery<Materia> query = cb.createQuery(Materia.class);
        Root<Materia> root = query.from(Materia.class);
        List<Predicate> predicates = buildSubjectPredicates(cb, root, subjectName, careerId, careerName);
        query.select(root).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Materia> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(page * elements);
        typedQuery.setMaxResults(elements);
        List<Materia> resultList = typedQuery.getResultList();

        List<Subject> dtoList = resultList.stream()
                .map(mapper::toSubject)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, PageRequest.of(page, elements), total);
    }

    @Override
    public List<SubjectFilter> filterByCareerId(int careerId) {
        return jpaRepository.findByCarreraId(careerId);
    }

    @Override
    public List<Integer> getMateriaIdsExisting(List<Integer> idSubjects){
        return jpaRepository.findExistingMateriaIds(idSubjects);
    }

    private List<Predicate> buildSubjectPredicates(CriteriaBuilder cb, Root<Materia> root,
                                                   String subjectName, Integer careerId, String careerName) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("isDeleted"), false));
        if (subjectName != null && !subjectName.isEmpty()) {
            String filtroMateria = normalize.apply(subjectName);

            Predicate subjectNameLike = cb.like(cb.function("TRANSLATE", String.class,
                            cb.lower(root.get("nombreMateria")),
                            cb.literal("áéíóúüñ"), cb.literal("aeiouun")),
                    "%" + filtroMateria + "%");

            predicates.add(subjectNameLike);
        }

        if (careerId != null) {
            predicates.add(cb.equal(root.get("carrera").get("idCarrera"), careerId));
        }

        if (careerName != null && !careerName.trim().isEmpty()) {
            Join<Subject, Career> careerJoin = root.join("carrera");

            String filtroCarrera = normalize.apply(careerName);

            Predicate careerNameLike = cb.like(cb.function("TRANSLATE", String.class,
                            cb.lower(careerJoin.get("nombreCarrera")),
                            cb.literal("áéíóúüñ"), cb.literal("aeiouun")),
                    "%" + filtroCarrera + "%");

            predicates.add(careerNameLike);
        }

        return predicates;
    }

    private final Function<String, String> normalize = text -> text.toLowerCase()
            .replace("á", "a").replace("é", "e").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ü", "u")
            .replace("ñ", "n");

    @Override
    public void create(CreateSubjectDTO subject) {
        boolean yaExiste = jpaRepository.findByNombreMateriaAndIsDeletedFalse(subject.getSubjectName()).isPresent();
        boolean codigoExistente = jpaRepository.findByCodigoAndIsDeletedFalse(subject.getCode()).isPresent();
        if (yaExiste) {
            throw new RuntimeException("La materia ya se encuentra registrada");
        } else if(codigoExistente){
            throw new RuntimeException("El codigo ya esta asociado a otra materia");
        }

        Carrera carrera = carreraCrudRepository.findById(subject.getCareerId())
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada"));

        Materia materia = new Materia();

        materia.setIdCarrera(subject.getCareerId());
        materia.setCarrera(carrera);
        materia.setNombreMateria(subject.getSubjectName());
        materia.setCodigo(subject.getCode());
        materia.setCreditos(subject.getCredits());
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
