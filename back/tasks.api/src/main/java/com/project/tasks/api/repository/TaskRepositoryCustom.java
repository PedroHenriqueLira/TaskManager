package com.project.tasks.api.repository;

import com.project.tasks.api.model.Tarefa;
import com.project.tasks.api.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface TaskRepositoryCustom {
    Page<Tarefa> findByFilters(Map<String, String> filtros, Pageable pageable, Usuario usuarioLogado);
}
