package com.tutorias.persistance.repository;

import com.tutorias.domain.dto.CreateAvailabilityDTO;
import com.tutorias.domain.model.Availability;
import com.tutorias.domain.repository.AvailabilityRepository;
import com.tutorias.persistance.crud.DisponibilidadCrudRepository;
import com.tutorias.persistance.crud.SalonCrudRepository;
import com.tutorias.persistance.entity.Disponibilidad;
import com.tutorias.persistance.entity.Salon;
import com.tutorias.persistance.mapper.AvailabilityMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class DisponibilidadRepository implements AvailabilityRepository {
    @Autowired
    private DisponibilidadCrudRepository jpaRepository;
    @Autowired
    private SalonCrudRepository salonCrudRepository;
    @Autowired
    private AvailabilityMapper mapper;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Availability> getAll() {
        List<Disponibilidad> disponibilidades = jpaRepository.findAll();
        return mapper.toAvailabilities(disponibilidades);
    }

    @Override
    public Optional<Availability> getById(int availabilityId) {
        return jpaRepository.findById(availabilityId)
                .map(mapper::toAvailability);
    }

    @Override
    public List<Availability> filterAvailability(int classroomId, String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Disponibilidad> query = cb.createQuery(Disponibilidad.class);
        Root<Disponibilidad> root = query.from(Disponibilidad.class);
        List<Predicate> predicates = buildPredicates(cb, root, classroomId, dayOfWeek, startTime, endTime);
        query.select(root).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Disponibilidad> typedQuery = entityManager.createQuery(query);
        List<Disponibilidad> results = typedQuery.getResultList();

        return results.stream()
                .map(mapper::toAvailability)
                .collect(Collectors.toList());
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Disponibilidad> root,
                                            Integer classroomId, String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        List<Predicate> predicates = new ArrayList<>();

        if (classroomId != null) {
            predicates.add(cb.equal(root.get("salon").get("idSalon"), classroomId));
        }

        if (dayOfWeek != null && !dayOfWeek.trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("diaSemana")), dayOfWeek.trim().toLowerCase()));
        }

        if (startTime != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("horaInicio"), startTime));
        }

        if (endTime != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("horaFin"), endTime));
        }

        return predicates;
    }

    @Override
    public void create(CreateAvailabilityDTO availability) {
        Salon salon = salonCrudRepository.findById(availability.getClassroomId())
                .orElseThrow(() -> new RuntimeException("Salón no encontrado"));

        Disponibilidad disponibilidad = new Disponibilidad();

        disponibilidad.setSalon(salon);
        disponibilidad.setDiaSemana(availability.getDayOfWeek());
        disponibilidad.setHoraInicio(availability.getStartTime());
        disponibilidad.setHoraFin(availability.getEndTime());
        jpaRepository.save(disponibilidad);
    }

    @Override
    public void update(int availabilityId, CreateAvailabilityDTO availability) {
        Disponibilidad disponibilidad = jpaRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada"));

        if (availability.getClassroomId() != 0) {
            Salon salon = salonCrudRepository.findById(availability.getClassroomId())
                    .orElseThrow(() -> new RuntimeException("Salón no encontrado"));
            disponibilidad.setSalon(salon);
        }

        if (availability.getDayOfWeek() != null) {
            disponibilidad.setDiaSemana(availability.getDayOfWeek());
        }

        if (availability.getStartTime() != null) {
            disponibilidad.setHoraInicio(availability.getStartTime());
        }

        if (availability.getEndTime() != null) {
            disponibilidad.setHoraFin(availability.getEndTime());
        }

        jpaRepository.save(disponibilidad);
    }

    @Override
    public void delete(int availabilityId) {
        Disponibilidad disponibilidad = jpaRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada"));

        jpaRepository.deleteById(disponibilidad.getIdDisponibilidad());
    }

    @Override
    public void updateOccupied(int availabilityId) {
        Disponibilidad disponibilidad = jpaRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada"));

        disponibilidad.setOcupado(!disponibilidad.getOcupado());
    }
}
