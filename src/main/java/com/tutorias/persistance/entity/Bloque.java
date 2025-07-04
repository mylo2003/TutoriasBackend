package com.tutorias.persistance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "bloques")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bloque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bloque")
    private Integer idBloque;

    @Column(name = "nombre_bloque", nullable = false, length = 50)
    private String nombreBloque;

    @Column(name = "seccion", length = 10)
    private String seccion;

    @OneToMany(mappedBy = "bloque", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Salon> salones;
}
