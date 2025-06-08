package com.tutorias.domain.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface ResponseScheduleDTO {
    Integer getIdHorario();
    SalonInfo getSalon();
    UsuarioInfo getUsuario();
    MateriaInfo getMateria();
    String getDescripcion();
    LocalDateTime getFechaHorario();
    LocalTime getHoraInicio();
    LocalTime getHoraFin();
    String getModo();
    String getTipo();
    List<AgendadoInfo> getAgendados();
    interface SalonInfo {
        String getDescripcion();
        String getUbicacion();
        BloqueInfo getBloque();
    }

    interface BloqueInfo {
        String getSeccion();
    }

    interface UsuarioInfo {
        Integer getIdUsuario();
    }

    interface MateriaInfo {
        String getNombreMateria();
    }

    interface AgendadoInfo {
        Integer getId(); // ejemplo
    }
}


