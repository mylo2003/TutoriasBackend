package com.tutorias.persistance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_carrera")
    private Integer idCarrera;

    @Column(name = "id_rol")
    private Integer idRol;

    private String nombre;

    private String apellido;

    private String usuario;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    private Integer semestre;

    @Column(name = "valoracion_promedio")
    private Double valoracionPromedio;

    private String contrasenia;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera", insertable = false, updatable = false)
    private Carrera carrera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", insertable = false, updatable = false)
    private Rol rol;

    @OneToMany(mappedBy = "usuario")
    private List<MateriaUsuario> materiaUsuarios;
}
