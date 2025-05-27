package com.tutorias.persistance.mapper;

import com.tutorias.domain.model.Role;
import com.tutorias.persistance.entity.Rol;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mappings({
            @Mapping(source = "idRol", target = "roleId"),
            @Mapping(source = "nombreRol", target = "name")
    })
    Role toRole(Rol rol);
    List<Role> toRoles(List<Rol> roles);

    @InheritInverseConfiguration
    Rol toRol(Role role);
}
