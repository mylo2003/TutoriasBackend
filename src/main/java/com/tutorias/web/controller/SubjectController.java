package com.tutorias.web.controller;

import com.tutorias.domain.dto.CreateSubjectDTO;
import com.tutorias.domain.model.Subject;
import com.tutorias.domain.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/materias")
public class SubjectController {
    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<Subject>> getSubjects() {
        try {
            List<Subject> subjectList = subjectService.getAll();

            if (subjectList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(subjectList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{idMateria}")
    public ResponseEntity<?> getSubject(@PathVariable int idMateria) {
        try {
            Optional<Subject> subject = subjectService.getById(idMateria);
            if (subject.isPresent()) {
                return ResponseEntity.ok(subject.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Materia no encontrada"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createSubject(@RequestBody CreateSubjectDTO subject) {
        try {
            subjectService.createSubject(subject);
            return ResponseEntity.status(HttpStatus.CREATED).body("Materia creada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @DeleteMapping("/{idMateria}")
    public ResponseEntity<?> deleteSubject(@PathVariable int idMateria) {
        try {
            subjectService.deleteSubject(idMateria);
            return ResponseEntity.status(HttpStatus.OK).body("Materia eliminada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}
