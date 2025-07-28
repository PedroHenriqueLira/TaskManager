package com.project.tasks.api.model;

import com.project.tasks.api.enums.Prioridade;
import com.project.tasks.api.enums.StatusTarefa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "tarefas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "responsavel_id", foreignKey = @ForeignKey(name = "fk_responsavel"))
    private Usuario responsavel;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatusTarefa status;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @Column(name = "data_update", nullable = false)
    private LocalDateTime dataUpdate = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        this.dataUpdate = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        this.status = StatusTarefa.ANDAMENTO;
    }

    @PreUpdate
    public void preUpdate() {
        dataUpdate = LocalDateTime.now();
    }

}
