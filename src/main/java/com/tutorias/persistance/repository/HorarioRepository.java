package com.tutorias.persistance.repository;

import com.tutorias.domain.dto.CreateScheduleDTO;
import com.tutorias.domain.dto.ResponseScheduleDTO;
import com.tutorias.domain.dto.ResponseScheduleEditDTO;
import com.tutorias.domain.dto.ResponseScheduleFilterDTO;
import com.tutorias.domain.model.Availability;
import com.tutorias.domain.model.Schedule;
import com.tutorias.domain.repository.AvailabilityRepository;
import com.tutorias.domain.repository.ScheduleRepository;
import com.tutorias.persistance.crud.*;
import com.tutorias.persistance.entity.*;
import com.tutorias.persistance.mapper.ScheduleMapper;
import com.tutorias.persistance.mapper.ScheduleResponseMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
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
    private DisponibilidadCrudRepository disponibilidadCrudRepository;
    @Autowired
    private ScheduleMapper mapper;
    @Autowired
    private ScheduleResponseMapper mapperDTO;
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
    public Optional<ResponseScheduleEditDTO> getByIdToEdit(int scheduleId) {
        return jpaRepository.findById(scheduleId)
                .map(horario -> {
                    Salon salon = horario.getSalon();
                    Bloque bloque = salon.getBloque();

                    String dayOfWeek = obtenerDiaSemana(horario.getFechaHorario());

                    // Buscar disponibilidad usando tu método filterAvailability
                    List<Availability> disponibilidades = availabilityRepository.filterAvailability(
                            salon.getIdSalon(),
                            dayOfWeek,
                            horario.getHoraInicio(),
                            horario.getHoraFin()
                    );

                    Integer availabilityId = disponibilidades.stream()
                            .findFirst()
                            .map(Availability::getAvailabilityId) // o getIdDisponibilidad dependiendo de tu DTO
                            .orElse(null);

                    return ResponseScheduleEditDTO.builder()
                            .scheduleId(horario.getIdHorario())
                            .classroomId(salon.getIdSalon())
                            .blockId(bloque.getIdBloque())
                            .subjectId(horario.getMateria().getIdMateria())
                            .userId(horario.getUsuario().getIdUsuario())
                            .description(horario.getDescripcion())
                            .scheduleDate(horario.getFechaHorario())
                            .startTime(horario.getHoraInicio())
                            .endTime(horario.getHoraFin())
                            .mode(horario.getModo())
                            .type(horario.getTipo())
                            .availabilityId(availabilityId)
                            .build();
                });
    }

    @Override
    public Optional<ResponseScheduleFilterDTO> getByIdToMessage(int scheduleId) {
        Horario horario = jpaRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("No se encontró el horario con ID: " + scheduleId));

        return Optional.of(mapperDTO.toResponseScheduleDTO(horario));
    }


    private String obtenerDiaSemana(LocalDate fecha) {
        DayOfWeek dayOfWeek = fecha.getDayOfWeek();
        switch (dayOfWeek) {
            case MONDAY: return "LUNES";
            case TUESDAY: return "MARTES";
            case WEDNESDAY: return "MIERCOLES";
            case THURSDAY: return "JUEVES";
            case FRIDAY: return "VIERNES";
            case SATURDAY: return "SABADO";
            case SUNDAY: return "DOMINGO";
            default: throw new IllegalArgumentException("Día de la semana no válido");
        }
    }

    @Override
    public Map<String, List<ResponseScheduleFilterDTO>> obtenerHorariosPorUsuario(Integer idUsuario) {
        Usuario usuario = usuarioCrudRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean esProfesor = usuario.getIdRol() == 2;
        Map<String, List<ResponseScheduleFilterDTO>> response = new HashMap<>();

        if (esProfesor) {
            List<Horario> activos = jpaRepository.findByUsuario_IdUsuarioAndIsDeletedFalseAndModoInOrderByFechaHorarioAsc(
                    idUsuario, Arrays.asList("DISPONIBLE", "EN_CURSO"));
            List<Horario> finalizados = jpaRepository.findTop15ByUsuario_IdUsuarioAndIsDeletedFalseAndModoOrderByFechaHorarioDesc(
                    idUsuario, "FINALIZADO");

            response.put("activos", mapHorariosToDtoList(activos));
            response.put("finalizados", mapHorariosToDtoList(finalizados));

        } else {
            Set<Integer> materiasEstudiante = usuario.getMateriaUsuarios()
                    .stream()
                    .map(mu -> mu.getMateria().getIdMateria())
                    .collect(Collectors.toSet());

            List<Horario> horariosRelacionados = jpaRepository.findHorariosRelacionadosConMaterias(usuario.getIdUsuario(),
                    materiasEstudiante);

            List<Horario> agendados = jpaRepository.findHorariosAgendadosPorEstudiante(idUsuario);

            List<Horario> finalizadas = jpaRepository.findUltimas15FinalizadasDelEstudiante(idUsuario);

            response.put("disponibles", mapHorariosToDtoList(horariosRelacionados));
            response.put("agendados", mapHorariosToDtoList(agendados));
            response.put("finalizados", mapHorariosToDtoList(finalizadas));
        }

        return response;
    }

    private List<ResponseScheduleFilterDTO> mapHorariosToDtoList(List<Horario> horarios) {
        return horarios.stream()
                .map(mapperDTO::toResponseScheduleDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void create(CreateScheduleDTO schedule) {
        Materia materia = materiaCrudRepository.findById(schedule.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        Usuario usuario = usuarioCrudRepository.findById(schedule.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Horario horario = new Horario();

        horario.setMateria(materia);
        horario.setUsuario(usuario);
        horario.setDescripcion(schedule.getDescription());
        horario.setFechaHorario(schedule.getScheduleDate());
        horario.setModo("DISPONIBLE");
        horario.setTipo(schedule.getType());

        if ("PRESENCIAL".equalsIgnoreCase(schedule.getType())) {
            Disponibilidad disponibilidad = disponibilidadCrudRepository.findById(schedule.getAvailabilityId())
                    .orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada"));

            horario.setSalon(disponibilidad.getSalon());
            horario.setHoraInicio(disponibilidad.getHoraInicio());
            horario.setHoraFin(disponibilidad.getHoraFin());

        } else if ("VIRTUAL".equalsIgnoreCase(schedule.getType())) {
            Salon salon = salonCrudRepository.findById(0)
                    .orElseThrow(() -> new RuntimeException("Salon no encontrado"));
            horario.setSalon(salon);
            horario.setHoraInicio(schedule.getStartTime());
            horario.setHoraFin(schedule.getEndTime());
        } else {
            throw new RuntimeException("Tipo de asesoría no válido");
        }

        jpaRepository.save(horario);
    }

    @Override
    public void update(int scheduleId, CreateScheduleDTO schedule) {
        Horario horario = jpaRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        if (schedule.getType() != null) {
            horario.setTipo(schedule.getType().toUpperCase());
            if ("PRESENCIAL".equalsIgnoreCase(schedule.getType())) {
                if (schedule.getAvailabilityId() != 0) {
                    Disponibilidad disponibilidad = disponibilidadCrudRepository.findById(schedule.getAvailabilityId())
                            .orElseThrow(() -> new RuntimeException("Disponibilidad no encontrada"));
                    horario.setSalon(disponibilidad.getSalon());
                    horario.setHoraInicio(disponibilidad.getHoraInicio());
                    horario.setHoraFin(disponibilidad.getHoraFin());
                }
            } else if ("VIRTUAL".equalsIgnoreCase(schedule.getType())) {
                Salon salon = salonCrudRepository.findById(0)
                        .orElseThrow(() -> new RuntimeException("Salon virtual no encontrado"));
                horario.setSalon(salon);
                if (schedule.getStartTime() != null) {
                    horario.setHoraInicio(schedule.getStartTime());
                }
                if (schedule.getEndTime() != null) {
                    horario.setHoraFin(schedule.getEndTime());
                }
            } else {
                throw new RuntimeException("Tipo de asesoría no válido");
            }
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
    public List<ResponseScheduleDTO> getAllByUserId(int userId) {
        return jpaRepository.findAllByUsuario_IdUsuarioAndIsDeletedFalseAndModoNot(userId, "FINALIZADO");
    }
}
