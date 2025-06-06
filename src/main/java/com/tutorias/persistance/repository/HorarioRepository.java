package com.tutorias.persistance.repository;

import com.tutorias.domain.dto.CreateScheduleDTO;
import com.tutorias.domain.model.Schedule;
import com.tutorias.domain.repository.AvailabilityRepository;
import com.tutorias.domain.repository.ScheduleRepository;
import com.tutorias.persistance.crud.*;
import com.tutorias.persistance.entity.*;
import com.tutorias.persistance.mapper.ScheduleMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    private DisponibilidadCrudRepository disponibilidadCrudRepository;
    @Autowired
    private ScheduleMapper mapper;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private AvailabilityRepository availabilityRepository;

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
    public List<Schedule> filterSchedule(Integer subjectId, Integer classroomId, LocalDate date, String mode, String dayOfWeek) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Horario> query = cb.createQuery(Horario.class);
        Root<Horario> root = query.from(Horario.class);
        List<Predicate> predicates = buildHorarioPredicates(cb, root, subjectId, classroomId, date, mode, dayOfWeek);
        query.select(root).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Horario> typedQuery = entityManager.createQuery(query);
        List<Horario> resultList = typedQuery.getResultList();

        return resultList.stream()
                .map(mapper::toSchedule)
                .collect(Collectors.toList());
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
        Disponibilidad disponibilidad = disponibilidadCrudRepository.findById(schedule.getAvailabilityId())
                .orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada"));

        Materia materia = materiaCrudRepository.findById(schedule.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        Usuario usuario = usuarioCrudRepository.findById(schedule.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Horario horario = new Horario();

        horario.setSalon(disponibilidad.getSalon());
        horario.setMateria(materia);
        horario.setUsuario(usuario);
        horario.setDescripcion(schedule.getDescription());
        horario.setFechaHorario(schedule.getScheduleDate());
        horario.setHoraInicio(disponibilidad.getHoraInicio());
        horario.setHoraFin(disponibilidad.getHoraFin());

        availabilityRepository.updateOccupied(schedule.getAvailabilityId());
        horario.setModo("DISPONIBLE");
        jpaRepository.save(horario);
    }

    @Override
    public void update(int scheduleId, CreateScheduleDTO schedule) {
        Horario horario = jpaRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        if (schedule.getAvailabilityId() != 0) {
            Disponibilidad disponibilidad = disponibilidadCrudRepository.findById(schedule.getAvailabilityId())
                    .orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada"));
            horario.setSalon(disponibilidad.getSalon());
            horario.setHoraInicio(disponibilidad.getHoraInicio());
            horario.setHoraFin(disponibilidad.getHoraFin());
            availabilityRepository.updateOccupied(schedule.getAvailabilityId());
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

    @Override
    public List<Schedule> getAllByUserId(int userId) {
        return mapper.toSchedules(jpaRepository.findAllByUsuario_IdUsuarioAndIsDeletedFalseAndModoNot(userId, "FINALIZADO"));
    }
}
