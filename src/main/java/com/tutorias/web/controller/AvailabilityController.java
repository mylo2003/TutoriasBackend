package com.tutorias.web.controller;

import com.tutorias.domain.dto.CreateAvailabilityDTO;
import com.tutorias.domain.dto.CustomResponse;
import com.tutorias.domain.dto.ResponseAvailabilityDTO;
import com.tutorias.domain.model.Availability;
import com.tutorias.domain.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/disponibilidad")
public class AvailabilityController {
    @Autowired
    private AvailabilityService availabilityService;

    @GetMapping
    public ResponseEntity<List<Availability>> getAvailabilities() {
        try {
            List<Availability> availabilityList = availabilityService.getAll();

            if (availabilityList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(availabilityList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{idDisponibilidad}")
    public ResponseEntity<?> getAvailability(@PathVariable int idDisponibilidad) {
        try {
            Optional<Availability> availability = availabilityService.getById(idDisponibilidad);
            if (availability.isPresent()) {
                return ResponseEntity.ok(availability.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Disponibilidad no encontrada"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/filtro")
    public ResponseEntity<?> filterAvailability(
            @RequestParam(required = false) Integer classroomId,
            @RequestParam(required = false) String dayOfWeek,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime) {

        List<Availability> availabilityPage = availabilityService.filterAvailability(
                classroomId, dayOfWeek, startTime, endTime);

        String message = availabilityPage.isEmpty() ? "No se encontraron disponibilidades." : "Disponibilidades encontradas.";
        CustomResponse<List<Availability>> response = new CustomResponse<>(message, availabilityPage);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createAvailability(@RequestBody CreateAvailabilityDTO availability) {
        try {
            availabilityService.createAvailability(availability);
            return ResponseEntity.status(HttpStatus.CREATED).body("Disponibilidad creada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @PutMapping("/{idDisponibilidad}")
    public ResponseEntity<?> updateSchedule(@PathVariable int idDisponibilidad, @RequestBody CreateAvailabilityDTO availability) {
        try {
            availabilityService.updateAvailability(idDisponibilidad, availability);
            return ResponseEntity.status(HttpStatus.OK).body("Disponibilidad actualizada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @DeleteMapping("/{idDisponibilidad}")
    public ResponseEntity<?> deleteAvailability(@PathVariable int idDisponibilidad) {
        try {
            availabilityService.deleteAvailability(idDisponibilidad);
            return ResponseEntity.status(HttpStatus.OK).body("Disponibilidad eliminada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @GetMapping("/salon/{salonId}")
    public ResponseEntity<List<ResponseAvailabilityDTO>> obtenerDisponibilidades(
            @PathVariable Integer salonId,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha) {

        try {
            List<ResponseAvailabilityDTO> disponibilidades =
                    availabilityService.obtenerDisponibilidades(fecha, salonId);

            return ResponseEntity.ok(disponibilidades);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
