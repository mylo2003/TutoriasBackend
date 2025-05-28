package com.tutorias.domain.repository;

import com.tutorias.domain.dto.CreateBlockDTO;
import com.tutorias.domain.model.Block;

import java.util.List;
import java.util.Optional;

public interface BlockRepository {
    List<Block> getAll();
    Optional<Block> getById(int blockId);
    void create(CreateBlockDTO block);
}
