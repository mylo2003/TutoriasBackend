package com.tutorias.domain.service;

import com.tutorias.domain.dto.CreateBlockDTO;
import com.tutorias.domain.model.Block;
import com.tutorias.domain.repository.BlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlockService {
    @Autowired
    private BlockRepository blockRepository;

    public List<Block> getAll() {
        return blockRepository.getAll();
    }

    public Optional<Block> getById(int blockId) {
        return blockRepository.getById(blockId);
    }

    public void createBlock(CreateBlockDTO block) {
        blockRepository.create(block);
    }
}
