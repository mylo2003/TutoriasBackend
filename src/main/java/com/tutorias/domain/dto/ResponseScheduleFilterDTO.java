package com.tutorias.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseScheduleFilterDTO {
    private Integer idHorario;
    private SalonInfo salon;
    private UsuarioInfo usuario;
    private MateriaInfo materia;
    private String descripcion;
    private LocalDate fechaHorario;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String modo;
    private String tipo;
    private List<AgendadoInfo> agendados;

    @Data
    public static class SalonInfo {
        private String descripcion;
        private String ubicacion;
        private BloqueInfo bloque;
    }

    @Data
    public static class BloqueInfo {
        private String seccion;
    }

    @Data
    public static class UsuarioInfo {
        private Integer idUsuario;
        private String nombre;
        private String apellido;
    }

    @Data
    public static class MateriaInfo {
        private String nombreMateria;
    }

    @Data
    public static class AgendadoInfo {
        private Integer id;
    }
}
