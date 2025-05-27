package com.tutorias.persistance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendados")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agendado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agendado")
    private Integer idAgendado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_horario", nullable = false)
    private Horario horario;

    @Column(name = "hora_agendado", nullable = false)
    private LocalDateTime horaAgendado;

    private Integer calificacion;

    @Column(name = "id_deleted")
    private Boolean idDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
