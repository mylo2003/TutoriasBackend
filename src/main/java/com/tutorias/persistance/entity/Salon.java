package com.tutorias.persistance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "salones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Salon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_salon")
    private Integer idSalon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bloque", nullable = false)
    private Bloque bloque;

    @Column(name = "descripcion", length = 100)
    private String descripcion;

    @Column(name = "ubicacion", length = 50)
    private String ubicacion;

    @Column(name = "capacidad")
    private String capacidad;

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Horario> horarios;

    @OneToMany(mappedBy = "salon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Disponibilidad> disponibilidades;
}