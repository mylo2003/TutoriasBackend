package com.tutorias.domain.service;

import com.tutorias.domain.dto.CreateBlockDTO;
import com.tutorias.domain.model.Block;
import com.tutorias.domain.model.User;
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
        List<Block> blocks = blockRepository.getAll();
        blocks.forEach(this::calculateTotalSalons);
        return blocks;
    }

    public Optional<Block> getById(int blockId) {
        Optional<Block> block = blockRepository.getById(blockId);
        block.ifPresent(this::calculateTotalSalons);
        return block;
    }

    public void createBlock(CreateBlockDTO block) {
        blockRepository.create(block);
    }

    public void deleteBlock(int blockId) {
        blockRepository.delete(blockId);
    }

    private void calculateTotalSalons(Block block) {
        if (!"Virtual".equals(block.getBlockName())) {
            block.setTotalSalons(blockRepository.countSalonsByIdBlock(block.getBlockId()));
        } else {
            block.setTotalSalons(0);
        }
    }
}
