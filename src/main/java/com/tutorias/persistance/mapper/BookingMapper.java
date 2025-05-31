package com.tutorias.persistance.mapper;

import com.tutorias.domain.model.Booking;
import com.tutorias.persistance.entity.Agendado;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mappings({
            @Mapping(source = "idAgendado", target = "bookingId"),
            @Mapping(source = "usuario.idUsuario", target = "userId"),
            @Mapping(source = "horario.idHorario", target = "scheduleId"),
            @Mapping(source = "horaAgendado", target = "scheduleDateTime"),
            @Mapping(source = "finalizado", target = "finished"),
            @Mapping(source = "calificacion", target = "rating")
    })
    Booking toBooking(Agendado agendado);

    List<Booking> toBookings(List<Agendado> agendados);

    @InheritInverseConfiguration
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "horario", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Agendado toAgendado(Booking booking);
}
