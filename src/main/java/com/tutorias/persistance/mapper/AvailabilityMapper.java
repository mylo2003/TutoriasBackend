package com.tutorias.persistance.mapper;

import com.tutorias.domain.model.Availability;
import com.tutorias.persistance.entity.Disponibilidad;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {
    @Mappings({
            @Mapping(source = "idDisponibilidad", target = "availabilityId"),
            @Mapping(source = "salon.idSalon", target = "classroomId"),
            @Mapping(source = "diaSemana", target = "dayOfWeek"),
            @Mapping(source = "horaInicio", target = "startTime"),
            @Mapping(source = "horaFin", target = "endTime"),
            @Mapping(source = "ocupado", target = "occupied")
    })
    Availability toAvailability(Disponibilidad disponibilidad);

    List<Availability> toAvailabilities(List<Disponibilidad> disponibilidades);

    @InheritInverseConfiguration
    @Mapping(target = "salon", ignore = true)
    Disponibilidad toDisponibilidad(Availability availability);
}