package com.tutorias.web.controller;

import com.tutorias.domain.dto.CreateBlockDTO;
import com.tutorias.domain.model.Block;
import com.tutorias.domain.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/bloques")
public class BlockController {
    @Autowired
    private BlockService blockService;

    @GetMapping
    public ResponseEntity<List<Block>> getBlokes() {
        try {
            List<Block> blockList = blockService.getAll();

            if (blockList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(blockList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{idBloque}")
    public ResponseEntity<?> getBlock(@PathVariable int idBloque) {
        try {
            Optional<Block> block = blockService.getById(idBloque);
            if (block.isPresent()) {
                return ResponseEntity.ok(block.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Bloque no encontrado"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createBlock(@RequestBody CreateBlockDTO block) {
        try {
            blockService.createBlock(block);
            return ResponseEntity.status(HttpStatus.CREATED).body("Bloque creado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @DeleteMapping("/{idBloque}")
    public ResponseEntity<?> deleteBlock(@PathVariable int idBloque) {
        try {
            blockService.deleteBlock(idBloque);
            return ResponseEntity.status(HttpStatus.OK).body("Bloque eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}
