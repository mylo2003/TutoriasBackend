package com.tutorias.persistance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "materias_usuarios")
public class MateriaUsuario {
    @EmbeddedId
    private MateriaUsuarioPK id;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario", nullable = false, insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne
    @MapsId("idMateria")
    @JoinColumn(name = "id_materia", nullable = false, insertable = false, updatable = false)
    private Materia materia;
}
