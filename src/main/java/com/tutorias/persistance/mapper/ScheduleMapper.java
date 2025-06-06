package com.tutorias.persistance.mapper;

import com.tutorias.domain.model.Schedule;
import com.tutorias.persistance.entity.Horario;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookingMapper.class})
public interface ScheduleMapper {
    @Mappings({
            @Mapping(source = "idHorario", target = "scheduleId"),
            @Mapping(source = "salon.idSalon", target = "classroomId"),
            @Mapping(source = "materia.idMateria", target = "subjectId"),
            @Mapping(source = "usuario.idUsuario", target = "userId"),
            @Mapping(source = "descripcion", target = "description"),
            @Mapping(source = "fechaHorario", target = "scheduleDate"),
            @Mapping(source = "horaInicio", target = "startTime"),
            @Mapping(source = "horaFin", target = "endTime"),
            @Mapping(source = "modo", target = "mode"),
            @Mapping(source = "tipo", target = "type"),
            @Mapping(source = "agendados", target = "bookings")
    })
    Schedule toSchedule(Horario horario);

    List<Schedule> toSchedules(List<Horario> horarios);

    @InheritInverseConfiguration
    @Mapping(target = "salon", ignore = true)
    @Mapping(target = "materia", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "agendados", ignore = true)
    Horario toHorario(Schedule schedule);
}
