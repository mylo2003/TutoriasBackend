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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public void delete(int scheduleId) {
        Horario horario = jpaRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        horario.setIsDeleted(true);
        horario.setDeletedAt(LocalDateTime.now());
        jpaRepository.save(horario);
    }
}
