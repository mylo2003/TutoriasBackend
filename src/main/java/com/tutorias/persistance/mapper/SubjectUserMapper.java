package com.tutorias.persistance.mapper;

import com.tutorias.domain.model.SubjectUser;
import com.tutorias.persistance.entity.MateriaUsuario;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SubjectMapper.class})
public interface SubjectUserMapper {
    @Mappings({
            @Mapping(source = "id.idUsuario", target = "userId"),
            @Mapping(source = "id.idMateria", target = "subjectId"),
            @Mapping(source = "materia", target = "subject"),
            @Mapping(target = "user", ignore = true)
    })
    SubjectUser toSubjectUser(MateriaUsuario materiaUsuario);
    List<SubjectUser> toSubjectUsers(List<MateriaUsuario> materiaUsuarios);

    @InheritInverseConfiguration
//    @Mapping(target = "id.idUsuario", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    MateriaUsuario toMateriaUsuario(SubjectUser subjectUser);
}
