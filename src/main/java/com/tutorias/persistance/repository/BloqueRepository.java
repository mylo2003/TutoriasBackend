package com.tutorias.persistance.repository;

import com.tutorias.domain.dto.CreateBlockDTO;
import com.tutorias.domain.model.Block;
import com.tutorias.domain.repository.BlockRepository;
import com.tutorias.persistance.crud.BloqueCrudRepository;
import com.tutorias.persistance.entity.Bloque;
import com.tutorias.persistance.entity.Disponibilidad;
import com.tutorias.persistance.mapper.BlockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BloqueRepository implements BlockRepository {
    @Autowired
    private BloqueCrudRepository jpaRepository;
    @Autowired
    private BlockMapper mapper;

    @Override
    public List<Block> getAll() {
        List<Bloque> bloques = jpaRepository.findAll();
        return mapper.toBlocks(bloques);
    }

    @Override
    public Optional<Block> getById(int blockId) {
        return jpaRepository.findById(blockId)
                .map(mapper::toBlock);
    }

    @Override
    public void create(CreateBlockDTO block) {
        boolean yaExiste = jpaRepository.findByNombreBloque(block.getBlockName()).isPresent();

        if (yaExiste) {
            throw new RuntimeException("El bloque ya se encuentra registrado");
        }

        Bloque bloque = new Bloque();

        bloque.setNombreBloque(block.getBlockName());
        bloque.setSeccion(block.getSection());
        jpaRepository.save(bloque);
    }

    @Override
    public void delete(int blockId) {
        Bloque bloque = jpaRepository.findById(blockId)
                .orElseThrow(() -> new RuntimeException("Bloque no encontrado"));

        jpaRepository.deleteById(bloque.getIdBloque());
    }
}
