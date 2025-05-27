package com.tutorias.persistance.mapper;

import com.tutorias.domain.model.Career;
import com.tutorias.persistance.entity.Carrera;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CareerMapper {
    @Mappings({
            @Mapping(source = "idCarrera", target = "careerId"),
            @Mapping(source = "nombreCarrera", target = "careerName")
    })
    Career toCareer(Carrera carrera);
    List<Career> toCareers(List<Carrera> carreras);

    @InheritInverseConfiguration
    Carrera toCarrera(Career career);
}
