package com.tutorias.web.controller;

import com.tutorias.domain.dto.CreateScheduleDTO;
import com.tutorias.domain.dto.ResponseScheduleDTO;
import com.tutorias.domain.dto.ResponseScheduleEditDTO;
import com.tutorias.domain.dto.ResponseScheduleFilterDTO;
import com.tutorias.domain.model.Schedule;
import com.tutorias.domain.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/horarios")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<Schedule>> getSchedules() {
        try {
            List<Schedule> scheduleList = scheduleService.getAll();

            if (scheduleList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(scheduleList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{idHorario}")
    public ResponseEntity<?> getSchedule(@PathVariable int idHorario) {
        try {
            Optional<Schedule> schedule = scheduleService.getById(idHorario);
            if (schedule.isPresent()) {
                return ResponseEntity.ok(schedule.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Horario no encontrado"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/edit/{idHorario}")
    public ResponseEntity<?> getScheduleToEdit(@PathVariable int idHorario) {
        try {
            Optional<ResponseScheduleEditDTO> schedule = scheduleService.getByIdToEdit(idHorario);
            if (schedule.isPresent()) {
                return ResponseEntity.ok(schedule.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Horario no encontrado"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/filtrar-por-usuario/{idUsuario}")
    public ResponseEntity<Map<String, List<ResponseScheduleFilterDTO>>> filtrarHorariosPorUsuario(@PathVariable Integer idUsuario) {
        Map<String, List<ResponseScheduleFilterDTO>> resultado = scheduleService.obtenerHorariosPorUsuario(idUsuario);
        if (resultado.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody CreateScheduleDTO schedule) {
        try {
            scheduleService.createSchedule(schedule);
            return ResponseEntity.status(HttpStatus.CREATED).body("Horario creado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @PutMapping("/{idHorario}")
    public ResponseEntity<?> updateSchedule(@PathVariable int idHorario, @RequestBody CreateScheduleDTO schedule) {
        try {
            scheduleService.updateSchedule(idHorario, schedule);
            return ResponseEntity.status(HttpStatus.OK).body("Horario actualizado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @DeleteMapping("/{idHorario}")
    public ResponseEntity<?> deleteSchedule(@PathVariable int idHorario) {
        try {
            scheduleService.deleteSchedule(idHorario);
            return ResponseEntity.status(HttpStatus.OK).body("Horario eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ResponseScheduleDTO>> getSchedulesByUserId(@PathVariable int idUsuario) {
        try {
            List<ResponseScheduleDTO> scheduleList = scheduleService.getAllByUserId(idUsuario);

            if (scheduleList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(scheduleList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
