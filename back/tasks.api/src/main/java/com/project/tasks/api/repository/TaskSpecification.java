package com.project.tasks.api.repository;

import com.project.tasks.api.enums.Prioridade;
import com.project.tasks.api.enums.StatusTarefa;
import com.project.tasks.api.exceptions.BadRequestException;
import com.project.tasks.api.model.Tarefa;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskSpecification {

    public static Specification<Tarefa> comFiltros(Map<String, String> filtros) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            filtros.forEach((campo, valor) -> {
                try {
                    switch (campo) {
                        case "id" -> predicates.add(cb.equal(root.get("id"), Long.parseLong(valor)));

                        case "responsavel_id" -> predicates.add(
                                cb.equal(root.get("responsavel").get("id"), Long.parseLong(valor))
                        );

                        case "titulo" -> predicates.add(
                                cb.like(cb.lower(root.get("titulo")), "%" + valor.toLowerCase() + "%")
                        );

                        case "descricao" -> predicates.add(
                                cb.like(cb.lower(root.get("descricao")), "%" + valor.toLowerCase() + "%")
                        );

                        case "prioridade" -> {
                            Prioridade prioridade = Prioridade.fromString(valor);
                            predicates.add(cb.equal(root.get("prioridade"), prioridade));
                        }

                        case "status" -> {
                            StatusTarefa status = StatusTarefa.fromString(valor);
                            predicates.add(cb.equal(root.get("status"), status));
                        }

                        case "deadline" -> {
                            LocalDate date = LocalDate.parse(valor, dateFormatter);
                            predicates.add(cb.equal(root.get("deadline"), date));
                        }

                        default -> throw new BadRequestException("Filtro inválido: " + campo);
                    }
                } catch (Exception e) {
                    throw new BadRequestException("Erro no filtro '" + campo + "': " + e.getMessage());
                }
            });

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
