package com.tutorias.web.controller;

import com.tutorias.domain.dto.CreateScheduleDTO;
import com.tutorias.domain.dto.CustomResponse;
import com.tutorias.domain.model.Schedule;
import com.tutorias.domain.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/filtro")
    public ResponseEntity<?> filterSchedule(
            @RequestParam(required = false) Integer subjectId,
            @RequestParam(required = false) Integer classroomId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String dayOfWeek) {

        List<Schedule> schedulePage = scheduleService.filterSchedule(
                subjectId, classroomId, date, mode, dayOfWeek);

        String message = schedulePage.isEmpty() ? "No se encontraron horarios." : "Horarios encontrados.";
        CustomResponse<List<Schedule>> response = new CustomResponse<>(message, schedulePage);

        return ResponseEntity.ok(response);
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
    public ResponseEntity<List<Schedule>> getSchedulesByUserId(@PathVariable int idUsuario) {
        try {
            List<Schedule> scheduleList = scheduleService.getAllByUserId(idUsuario);

            if (scheduleList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(scheduleList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
