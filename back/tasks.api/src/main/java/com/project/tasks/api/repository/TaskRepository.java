package com.project.tasks.api.repository;


import com.project.tasks.api.model.Tarefa;
import com.project.tasks.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Tarefa, Long>, JpaSpecificationExecutor<Tarefa> {
    List<Tarefa> findByResponsavel(Usuario responsavel);
}
