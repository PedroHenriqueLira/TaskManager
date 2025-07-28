package com.project.tasks.api.model;

import com.project.tasks.api.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @NotNull
    @Email
    @Size(max = 150)
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank
    @Column(name = "password", nullable = false, columnDefinition = "TEXT")
    private String password;

    @NotBlank
    @Size(min = 11, max = 20)
    @Column(name = "documento", nullable = false, length = 20)
    private String documento;

    @NotBlank
    @Size(max = 250)
    @Column(name = "endereco", length = 250)
    private String endereco;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    @Comment("Status do usuário (ATIVO ou INATIVO)")
    private Status status;

    @NotNull
    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @NotNull
    @Column(name = "data_update", nullable = false)
    private LocalDateTime dataUpdate;

    @NotNull
    @Size(max = 50)
    @Column(name = "tipo_user", nullable = false)
    private String tipoUser;


    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        this.dataUpdate = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        this.tipoUser = "USER";
        this.status = Status.ATIVO;
    }

    @PreUpdate
    public void preUpdate() {
        this.dataUpdate = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
    }
}
