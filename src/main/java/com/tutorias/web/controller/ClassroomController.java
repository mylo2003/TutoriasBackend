package com.tutorias.web.controller;

import com.tutorias.domain.dto.CreateClassroomDTO;
import com.tutorias.domain.model.Classroom;
import com.tutorias.domain.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/salones")
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;

    @GetMapping
    public ResponseEntity<List<Classroom>> getClassrooms() {
        try {
            List<Classroom> classroomList = classroomService.getAll();

            if (classroomList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(classroomList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{idSalon}")
    public ResponseEntity<?> getClassroom(@PathVariable int idSalon) {
        try {
            Optional<Classroom> classroom = classroomService.getById(idSalon);
            if (classroom.isPresent()) {
                return ResponseEntity.ok(classroom.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Salon no encontrado"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createClassroom(@RequestBody CreateClassroomDTO classroom) {
        try {
            classroomService.createClassroom(classroom);
            return ResponseEntity.status(HttpStatus.CREATED).body("Salón creado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}
