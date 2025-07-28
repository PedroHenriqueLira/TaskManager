package com.project.tasks.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskEditRequest {
    private String titulo;
    private String descricao;
    private String prioridade;
    private LocalDate deadline;
    private String status;
}
