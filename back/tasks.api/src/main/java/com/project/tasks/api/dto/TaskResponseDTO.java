package com.project.tasks.api.dto;


import com.project.tasks.api.model.Tarefa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private Long idResponsavel;
    private String prioridade;
    private LocalDate deadline;
    private String status;
    private String dataCadastro;
    private String dataUpdate;

    public static TaskResponseDTO converte(Tarefa task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitulo(task.getTitulo());
        dto.setDescricao(task.getDescricao());
        dto.setIdResponsavel(task.getResponsavel() != null ? task.getResponsavel().getId() : null);
        dto.setPrioridade(task.getPrioridade().name());
        dto.setDeadline(task.getDeadline());
        dto.setStatus(task.getStatus().name());
        dto.setDataCadastro(task.getDataCadastro().format(formatter));
        dto.setDataUpdate(task.getDataUpdate().format(formatter));
        return dto;
    }
}
