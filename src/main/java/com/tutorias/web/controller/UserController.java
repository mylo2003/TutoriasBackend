package com.tutorias.web.controller;

import com.tutorias.domain.dto.CreateUserDTO;
import com.tutorias.domain.dto.EditSubjectUserDTO;
import com.tutorias.domain.model.Career;
import com.tutorias.domain.model.User;
import com.tutorias.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{idUser}")
    public ResponseEntity<Object> getById(@PathVariable Integer idUser){
        try {
            Optional<User> user = userService.getById(idUser);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{idUser}")
    public ResponseEntity<?> updateUser(@PathVariable int idUser, @RequestBody CreateUserDTO body){
        try {
            userService.updateUser(idUser, body);
            return ResponseEntity.status(HttpStatus.OK).body("Usuario actualizado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @DeleteMapping("/subjects/{idUsuario}")
    public ResponseEntity<?> deleteUserSubjects(@PathVariable Integer idUsuario, @RequestBody EditSubjectUserDTO body) {
        try {
            body.setIdUser(idUsuario);
            userService.deleteUserSubjects(body);

            String message = body.getIdSubjects().size() == 1 ?
                    "Materia eliminada exitosamente" :
                    "Materias eliminadas exitosamente";

            return ResponseEntity.ok()
                    .body(Map.of(
                            "message", message,
                            "totalDeleted", body.getIdSubjects().size()
                    ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @DeleteMapping("/{idUser}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer idUser){
        try {
            userService.deleteUser(idUser);
            return ResponseEntity.status(HttpStatus.OK).body("Usuario eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}
