package com.tutorias.web.controller;

import com.tutorias.domain.dto.CreateCareerDTO;
import com.tutorias.domain.model.Career;
import com.tutorias.domain.service.CareerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/carreras")
public class CareerController {
    @Autowired
    private CareerService careerService;

    @GetMapping
    public ResponseEntity<List<Career>> getCareers() {
        try {
            List<Career> careerList = careerService.getAll();

            if (careerList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(careerList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{idCarrera}")
    public ResponseEntity<?> getCareer(@PathVariable int idCarrera) {
        try {
            Optional<Career> career = careerService.getById(idCarrera);
            if (career.isPresent()) {
                return ResponseEntity.ok(career.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Carrera no encontrada"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createCareer(@RequestBody CreateCareerDTO career) {
        try {
            careerService.createCareer(career);
            return ResponseEntity.status(HttpStatus.CREATED).body("Carrera creada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @DeleteMapping("/{idCarrera}")
    public ResponseEntity<?> deleteCareer(@PathVariable int idCarrera) {
        try {
            careerService.deleteCareer(idCarrera);
            return ResponseEntity.status(HttpStatus.OK).body("Carrera eliminada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}
