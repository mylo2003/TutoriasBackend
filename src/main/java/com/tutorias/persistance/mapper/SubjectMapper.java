package com.tutorias.persistance.mapper;

import com.tutorias.domain.model.Subject;
import com.tutorias.persistance.entity.Materia;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CareerMapper.class, UserMapper.class})
public interface SubjectMapper {
    @Mappings({
            @Mapping(source = "idMateria", target = "subjectId"),
            @Mapping(source = "nombreMateria", target = "subjectName"),
            @Mapping(source = "idCarrera", target = "careerId"),
            @Mapping(source = "carrera", target = "career"),
    })
    Subject toSubject(Materia materia);
    List<Subject> toSubjects(List<Materia> materias);

    @InheritInverseConfiguration
    @Mapping(target = "materiaUsuarios", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Materia toMateria(Subject subject);
}
