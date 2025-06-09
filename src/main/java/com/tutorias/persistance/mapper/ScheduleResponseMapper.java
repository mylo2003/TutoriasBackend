package com.tutorias.persistance.mapper;

import com.tutorias.domain.dto.ResponseScheduleFilterDTO;
import com.tutorias.persistance.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleResponseMapper {

    @Mappings({
            @Mapping(source = "idHorario", target = "idHorario"),
            @Mapping(source = "salon", target = "salon"),
            @Mapping(source = "usuario", target = "usuario"),
            @Mapping(source = "materia", target = "materia"),
            @Mapping(source = "descripcion", target = "descripcion"),
            @Mapping(source = "fechaHorario", target = "fechaHorario"),
            @Mapping(source = "horaInicio", target = "horaInicio"),
            @Mapping(source = "horaFin", target = "horaFin"),
            @Mapping(source = "modo", target = "modo"),
            @Mapping(source = "tipo", target = "tipo"),
            @Mapping(source = "agendados", target = "agendados")
    })
    ResponseScheduleFilterDTO toResponseScheduleDTO(Horario horario);

    @Mappings({
            @Mapping(source = "descripcion", target = "descripcion"),
            @Mapping(source = "ubicacion", target = "ubicacion"),
            @Mapping(source = "bloque", target = "bloque")
    })
    ResponseScheduleFilterDTO.SalonInfo toSalonInfo(Salon salon);

    @Mapping(source = "seccion", target = "seccion")
    ResponseScheduleFilterDTO.BloqueInfo toBloqueInfo(Bloque bloque);

    @Mapping(source = "idUsuario", target = "idUsuario")
    @Mapping(source = "nombre", target = "nombre")
    @Mapping(source = "apellido", target = "apellido")
    ResponseScheduleFilterDTO.UsuarioInfo toUsuarioInfo(Usuario usuario);

    @Mapping(source = "nombreMateria", target = "nombreMateria")
    ResponseScheduleFilterDTO.MateriaInfo toMateriaInfo(Materia materia);

    @Mapping(source = "idAgendado", target = "id")
    ResponseScheduleFilterDTO.AgendadoInfo toAgendadoInfo(Agendado agendado);

    List<ResponseScheduleFilterDTO.AgendadoInfo> toAgendadoInfoList(List<Agendado> agendados);
}