package com.tutorias.persistance.mapper;

import com.tutorias.domain.model.Block;
import com.tutorias.persistance.entity.Bloque;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ClassroomMapper.class})
public interface BlockMapper {
    @Mappings({
            @Mapping(source = "idBloque", target = "blockId"),
            @Mapping(source = "nombreBloque", target = "blockName"),
            @Mapping(source = "seccion", target = "section"),
            @Mapping(source = "salones", target = "classrooms")
    })
    Block toBlock(Bloque bloque);

    List<Block> toBlocks(List<Bloque> bloques);

    @InheritInverseConfiguration
    @Mapping(target = "salones", ignore = true)
    Bloque toBloque(Block block);
}
