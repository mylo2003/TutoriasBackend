package com.tutorias.config;

import com.tutorias.domain.dto.EstadoAsesoria;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class AutomataEstado {
    public EstadoAsesoria transitar(
            LocalDateTime ahora,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin
    ) {
        LocalDateTime inicio = LocalDateTime.of(fecha, horaInicio);
        LocalDateTime fin = LocalDateTime.of(fecha, horaFin);

        if (ahora.isBefore(inicio)) {
            return EstadoAsesoria.DISPONIBLE;
        } else if (!ahora.isBefore(inicio) && ahora.isBefore(fin)) {
            return EstadoAsesoria.EN_CURSO;
        } else {
            return EstadoAsesoria.FINALIZADO;
        }
    }
}

