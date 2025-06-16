package com.tutorias.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    @JsonProperty("roleID")
    @NotNull(message = "El rol es obligatorio")
    @Min(value = 1, message = "El rol debe ser entre 1 y 2 (1 = estudiante, 2 = profesor)")
    @Max(value = 3, message = "El rol debe ser entre 1 y 2 (1 = estudiante, 2 = profesor)")
    private Integer roleID;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    private String lastName;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 3, max = 30, message = "El usuario debe tener entre 3 y 30 caracteres")
    private String user;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(regexp = "^(?=.*[0-9]).{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres y contener al menos un número")
    private String password;

    @NotBlank(message = "El correo es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@unipamplona\\.edu\\.co$",
            message = "El correo debe terminar en @unipamplona.edu.co")
    private String email;

    @NotNull(message = "El semestre es obligatorio")
    private Integer semester;

    @NotNull(message = "Debe seleccionar al menos una materia")
    @Size(min = 1, message = "Debe seleccionar al menos una materia")
    private List<Integer> subjects;
}
