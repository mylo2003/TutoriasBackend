package com.tutorias.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectUserResponseDTO {
    private Integer userId;
    private String nameUser;
    private List<SubjectInfoDTO> subjects;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectInfoDTO {
        private Integer idMateria;
        private String nombreMateria;
        private String nombreCarrera;
    }
}
