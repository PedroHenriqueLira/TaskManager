package com.project.tasks.api.dto;

import com.project.tasks.api.enums.Prioridade;
import com.project.tasks.api.enums.StatusTarefa;
import com.project.tasks.api.model.Tarefa;
import lombok.Data;

import java.util.List;

@Data
public class MetricasDTO {
    private Integer prioridadeAlta;
    private Integer prioridadeMedia;
    private Integer prioridadeBaixa;
    private Integer emAndamento;
    private Integer concluido;

    public static MetricasDTO converter(List<Tarefa> tarefas) {
        MetricasDTO dto = new MetricasDTO();

        dto.prioridadeAlta = (int) tarefas.stream()
                .filter(t -> t.getPrioridade() == Prioridade.ALTA)
                .count();

        dto.prioridadeMedia = (int) tarefas.stream()
                .filter(t -> t.getPrioridade() == Prioridade.MEDIA)
                .count();

        dto.prioridadeBaixa = (int) tarefas.stream()
                .filter(t -> t.getPrioridade() == Prioridade.BAIXA)
                .count();

        dto.emAndamento = (int) tarefas.stream()
                .filter(t -> t.getStatus() == StatusTarefa.ANDAMENTO)
                .count();

        dto.concluido = (int) tarefas.stream()
                .filter(t -> t.getStatus() == StatusTarefa.CONCLUIDA)
                .count();

        return dto;
    }

}
