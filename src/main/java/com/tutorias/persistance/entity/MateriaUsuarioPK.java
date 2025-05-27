package com.tutorias.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MateriaUsuarioPK implements Serializable {
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_materia")
    private Integer idMateria;
}
