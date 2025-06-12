package com.tutorias.persistance.mapper;

import com.tutorias.domain.model.Classroom;
import com.tutorias.persistance.entity.Salon;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AvailabilityMapper.class})
public interface ClassroomMapper {
    @Mappings({
            @Mapping(source = "idSalon", target = "classroomId"),
            @Mapping(source = "bloque.idBloque", target = "blockId"),
            @Mapping(source = "descripcion", target = "description"),
            @Mapping(source = "ubicacion", target = "location")
    })
    Classroom toClassroom(Salon salon);

    List<Classroom> toClassrooms(List<Salon> salones);

    @InheritInverseConfiguration
    @Mapping(target = "bloque", ignore = true)
    @Mapping(target = "horarios", ignore = true)
    @Mapping(target = "disponibilidades", ignore = true)
    Salon toSalon(Classroom classroom);
}
