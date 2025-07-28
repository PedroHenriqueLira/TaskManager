package com.project.tasks.api.controller;

import com.project.tasks.api.dto.*;
import com.project.tasks.api.service.TaskService;
import com.project.tasks.api.utils.ResponsePadraoDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("/cadastrar")
    public ResponseEntity<TaskResponseDTO> cadastrarTask (@RequestBody @Valid TaskCadastroRequest dto){
        TaskResponseDTO result = taskService.cadastrarTask(dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<ResponsePadraoDTO> deletarTask(@PathVariable Long id){
        ResponsePadraoDTO result = taskService.deletarTask(id);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/editar/{id}")
    public ResponseEntity<TaskResponseDTO> editarTask(@PathVariable Long id, @RequestBody @Valid TaskEditRequest dto){
        TaskResponseDTO result = taskService.editarTask(id, dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/buscarByParametros")
    public ResponseEntity<PageResponseDTO<TaskResponseDTO>> buscarDadosPaginado(
            @RequestParam Map<String, String> filtros,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        PageResponseDTO<TaskResponseDTO> response = taskService.listarByParametros(filtros, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/buscarMetricas")
    public ResponseEntity<MetricasDTO> buscarMetricas() {
        MetricasDTO response = taskService.buscarMetricas();
        return ResponseEntity.ok(response);
    }


}
