package com.tutorias.persistance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "materias")
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_materia")
    private Integer idMateria;

    @Column(name = "id_carrera", nullable = false)
    private Integer idCarrera;

    @Column(name = "nombre_materia", nullable = false, length = 100)
    private String nombreMateria;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera", insertable = false, updatable = false)
    private Carrera carrera;

    @OneToMany(mappedBy = "materia")
    private List<MateriaUsuario> materiaUsuarios;
}
