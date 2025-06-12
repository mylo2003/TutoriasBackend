package com.tutorias.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAvailabilityDTO {
    private Integer idDisponibilidad;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
