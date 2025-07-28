package com.project.tasks.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "auditoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auditoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 255)
    private String usuario;

    @Column(name = "acao", nullable = false, length = 255)
    private String acao;

    @Column(name = "endpoint", nullable = false, length = 255)
    private String endpoint;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "status_code", nullable = false, updatable = false)
    private int statusCode;

    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
    }

}
