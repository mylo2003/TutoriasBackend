package com.tutorias.persistance.mapper;

import com.tutorias.domain.model.User;
import com.tutorias.persistance.entity.Usuario;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class, CareerMapper.class, SubjectUserMapper.class})
public interface UserMapper {
    @Mappings({
            @Mapping(source = "idUsuario", target = "userId"),
            @Mapping(source = "nombre", target = "name"),
            @Mapping(source = "apellido", target = "lastName"),
            @Mapping(source = "usuario", target = "username"),
            @Mapping(source = "correo", target = "email"),
            @Mapping(source = "semestre", target = "semester"),
            @Mapping(source = "valoracionPromedio", target = "averageRating"),
            @Mapping(target = "totalSchedule", ignore = true),
            @Mapping(source = "carrera", target = "career"),
            @Mapping(source = "rol", target = "role"),
            @Mapping(source = "materiaUsuarios", target = "subjectUsers")

    })
    User toUser(Usuario usuario);
    List<User> toUsers(List<Usuario> usuarios);

    @InheritInverseConfiguration
    @Mapping(target = "idCarrera", ignore = true)
    @Mapping(target = "idRol", ignore = true)
    @Mapping(target = "contrasenia", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
//    @Mapping(target = "totalSchedule", ignore = true)
    Usuario toUsuario(User user);
}
