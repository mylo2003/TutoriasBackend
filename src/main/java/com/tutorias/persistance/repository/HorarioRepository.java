package com.tutorias.persistance.repository;

import com.tutorias.domain.dto.CreateScheduleDTO;
import com.tutorias.domain.model.Schedule;
import com.tutorias.domain.repository.ScheduleRepository;
import com.tutorias.persistance.crud.HorarioCrudRepository;
import com.tutorias.persistance.crud.MateriaCrudRepository;
import com.tutorias.persistance.crud.SalonCrudRepository;
import com.tutorias.persistance.crud.UsuarioCrudRepository;
import com.tutorias.persistance.entity.Horario;
import com.tutorias.persistance.entity.Materia;
import com.tutorias.persistance.entity.Salon;
import com.tutorias.persistance.entity.Usuario;
import com.tutorias.persistance.mapper.ScheduleMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class HorarioRepository implements ScheduleRepository {
    @Autowired
    private HorarioCrudRepository jpaRepository;
    @Autowired
    private UsuarioCrudRepository usuarioCrudRepository;
    @Autowired
    private MateriaCrudRepository materiaCrudRepository;
    @Autowired
    private SalonCrudRepository salonCrudRepository;
    @Autowired
    private ScheduleMapper mapper;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Schedule> getAll() {
        List<Horario> horarios = jpaRepository.findAll();
        return mapper.toSchedules(horarios);
    }

    @Override
    public Optional<Schedule> getById(int scheduleId) {
        return jpaRepository.findById(scheduleId)
                .map(mapper::toSchedule);
    }

    @Override
    public Page<Schedule> filterSchedule(Integer subjectId, Integer classroomId, LocalDate date, String mode, String dayOfWeek, int page, int elements) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Horario> countRoot = countQuery.from(Horario.class);
        List<Predicate> countPredicates = buildHorarioPredicates(cb, countRoot, subjectId, classroomId, date, mode, dayOfWeek);
        countQuery.select(cb.countDistinct(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        CriteriaQuery<Horario> query = cb.createQuery(Horario.class);
        Root<Horario> root = query.from(Horario.class);
        List<Predicate> predicates = buildHorarioPredicates(cb, root, subjectId, classroomId, date, mode, dayOfWeek);
        query.select(root).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Horario> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(page * elements);
        typedQuery.setMaxResults(elements);

        List<Horario> resultList = typedQuery.getResultList();
        List<Schedule> scheduleList = resultList.stream()
                .map(mapper::toSchedule)
                .collect(Collectors.toList());

        return new PageImpl<>(scheduleList, PageRequest.of(page, elements), total);
    }

    private List<Predicate> buildHorarioPredicates(CriteriaBuilder cb, Root<Horario> root,
                                                   Integer subjectId, Integer classroomId,
                                                   LocalDate date, String mode, String dayOfWeek) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isFalse(root.get("isDeleted")));

        if (subjectId != null) {
            predicates.add(cb.equal(root.get("materia").get("idMateria"), subjectId));
        }

        if (classroomId != null) {
            predicates.add(cb.equal(root.get("salon").get("idSalon"), classroomId));
        }

        if (date != null) {
            predicates.add(cb.equal(cb.function("DATE", LocalDate.class, root.get("fechaHorario")), date));
        }

        if (mode != null && !mode.trim().isEmpty()) {
            String modoLower = mode.trim().toLowerCase();
            predicates.add(cb.equal(cb.lower(root.get("modo")), modoLower));

            if (modoLower.equals("finalizado")) {
                // Solo mostrar asesorías finalizadas de esta semana
                LocalDate now = LocalDate.now();
                LocalDate startOfWeek = now.with(java.time.DayOfWeek.MONDAY);
                LocalDate endOfWeek = now.with(java.time.DayOfWeek.SUNDAY);

                Expression<LocalDate> fechaSolo = cb.function("DATE", LocalDate.class, root.get("fechaHorario"));
                predicates.add(cb.between(fechaSolo, startOfWeek, endOfWeek));
            }
        } else {
            predicates.add(cb.notEqual(cb.lower(root.get("modo")), "finalizado"));
        }

        return predicates;
    }

    @Override
    public void create(CreateScheduleDTO schedule) {
        Salon salon = salonCrudRepository.findById(schedule.getClassroomId())
                .orElseThrow(() -> new RuntimeException("Salón no encontrado"));

        Materia materia = materiaCrudRepository.findById(schedule.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        Usuario usuario = usuarioCrudRepository.findById(schedule.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Horario horario = new Horario();

        horario.setSalon(salon);
        horario.setMateria(materia);
        horario.setUsuario(usuario);
        horario.setDescripcion(schedule.getDescription());
        horario.setFechaHorario(schedule.getScheduleDate());
        horario.setHoraInicio(schedule.getStartTime());
        horario.setHoraFin(schedule.getEndTime());
        horario.setModo("DISPONIBLE");
        jpaRepository.save(horario);
    }

    @Override
    public void update(int scheduleId, CreateScheduleDTO schedule) {
        Horario horario = jpaRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        if (schedule.getClassroomId() != 0) {
            Salon salon = salonCrudRepository.findById(schedule.getClassroomId())
                    .orElseThrow(() -> new RuntimeException("Salón no encontrado"));
            horario.setSalon(salon);
        }

        if (schedule.getSubjectId() != 0) {
            Materia materia = materiaCrudRepository.findById(schedule.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
            horario.setMateria(materia);
        }

        if (schedule.getUserId() != 0) {
            Usuario usuario = usuarioCrudRepository.findById(schedule.getUserId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            horario.setUsuario(usuario);
        }

        if (schedule.getDescription() != null) {
            horario.setDescripcion(schedule.getDescription());
        }

        if (schedule.getScheduleDate() != null) {
            horario.setFechaHorario(schedule.getScheduleDate());
        }

        if (schedule.getStartTime() != null) {
            horario.setHoraInicio(schedule.getStartTime());
        }

        if (schedule.getEndTime() != null) {
            horario.setHoraFin(schedule.getEndTime());
        }

        jpaRepository.save(horario);
    }

    @Override
    public void delete(int scheduleId) {
        Horario horario = jpaRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        horario.setIsDeleted(true);
        horario.setDeletedAt(LocalDateTime.now());
        horario.setModo("FINALIZADO");
        jpaRepository.save(horario);
    }

    @Override
    public void updateMode(int scheduleId, String mode) {
        Horario horario = jpaRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        horario.setModo(mode);
        jpaRepository.save(horario);
    }

    @Override
    public List<Schedule> findAllByIsDeletedFalse() {
        return mapper.toSchedules(jpaRepository.findAllByIsDeletedFalseAndModoNot("FINALIZADO"));
    }
}
