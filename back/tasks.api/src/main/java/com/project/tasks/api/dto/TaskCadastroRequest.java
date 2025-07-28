package com.project.tasks.api.dto;

import com.project.tasks.api.enums.Prioridade;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCadastroRequest {
    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotNull(message = "A descricao é obrigatória")
    private String descricao;

    @NotNull(message = "A prioridade é obrigatória")
    private String prioridade;

    @NotNull(message = "A data limite é obrigatória")
    @FutureOrPresent(message = "A data limite deve ser hoje ou uma data futura")
    private LocalDate deadline;
}
