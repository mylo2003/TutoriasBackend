package com.tutorias.web.controller;

import com.tutorias.domain.dto.SubjectFilter;
import com.tutorias.domain.service.SubjectUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/materiasUsuario")
public class SubjectUserController {
    @Autowired
    private SubjectUserService subjectUserService;

    @GetMapping("/{idUsuario}")
    public ResponseEntity<List<SubjectFilter>> getSubjectsByUserId(@PathVariable int idUsuario) {
        try {
            List<SubjectFilter> subjectList = subjectUserService.filterByUserId(idUsuario);

            if (subjectList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(subjectList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
