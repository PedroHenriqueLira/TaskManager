package com.project.tasks.api.service;

import com.project.tasks.api.configs.security.JwtTokenUtil;
import com.project.tasks.api.dto.*;
import com.project.tasks.api.enums.Prioridade;
import com.project.tasks.api.enums.StatusTarefa;
import com.project.tasks.api.exceptions.BadRequestException;
import com.project.tasks.api.exceptions.InvalidParametersException;
import com.project.tasks.api.exceptions.NotFoundException;
import com.project.tasks.api.model.Tarefa;
import com.project.tasks.api.model.Usuario;
import com.project.tasks.api.repository.TaskRepository;
import com.project.tasks.api.repository.TaskSpecification;
import com.project.tasks.api.utils.MsgCods;
import com.project.tasks.api.utils.ResponsePadraoDTO;
import com.project.tasks.api.utils.UtilFunctions;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Transactional(rollbackOn = Exception.class)
    public TaskResponseDTO cadastrarTask(TaskCadastroRequest dto) {
        Prioridade prioridade = Prioridade.fromString(dto.getPrioridade());
        Usuario usuario = jwtTokenUtil.getUsuarioLogado();
        Tarefa task = new Tarefa();
        task.setTitulo(dto.getTitulo());
        task.setDescricao(dto.getDescricao());
        task.setDeadline(dto.getDeadline());
        task.setPrioridade(prioridade);
        task.setResponsavel(usuario);
        task.preUpdate();
        taskRepository.save(task);
        return TaskResponseDTO.converte(task);
    }

    public ResponsePadraoDTO deletarTask(Long id) {
        Usuario usuario = jwtTokenUtil.getUsuarioLogado();
        boolean isAdmin = jwtTokenUtil.isAdmin(usuario);
        Tarefa task = taskRepository.findById(id).orElseThrow(()-> new NotFoundException(new MsgCods().getCodigoErro(4)));

        if(!task.getResponsavel().equals(usuario) && !isAdmin) {
            throw new BadRequestException(new MsgCods().getCodigoErro(5));
        }

        taskRepository.delete(task);
        return ResponsePadraoDTO.sucesso("Operação realizada com sucesso.");
    }

    public TaskResponseDTO editarTask(Long id, TaskEditRequest dto) {
        Usuario usuario = jwtTokenUtil.getUsuarioLogado();
        boolean isAdmin = jwtTokenUtil.isAdmin(usuario);

        Tarefa tarefa = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new MsgCods().getCodigoErro(6)));

        if (!tarefa.getResponsavel().equals(usuario) && !isAdmin) {
            throw new BadRequestException(new MsgCods().getCodigoErro(7));
        }

        if (dto.getTitulo() != null && !dto.getTitulo().isBlank()) {
            tarefa.setTitulo(dto.getTitulo());
        }

        if (dto.getDescricao() != null && !dto.getDescricao().isBlank()) {
            tarefa.setDescricao(dto.getDescricao());
        }

        if (dto.getPrioridade() != null && !dto.getPrioridade().isBlank()) {
            Prioridade prioridade = Prioridade.fromString(dto.getPrioridade());
            tarefa.setPrioridade(prioridade);
        }

        if (dto.getDeadline() != null) {
            if (dto.getDeadline().isBefore(LocalDate.now())) {
                throw new BadRequestException(new MsgCods().getCodigoErro(8));
            }
            tarefa.setDeadline(dto.getDeadline());
        }

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            StatusTarefa status = StatusTarefa.fromString(dto.getStatus());
            tarefa.setStatus(status);
        }

        tarefa.preUpdate();
        taskRepository.save(tarefa);

        return TaskResponseDTO.converte(tarefa);
    }

    public PageResponseDTO<TaskResponseDTO> listarByParametros(Map<String, String> filtros, int page, int size) {
        Map<String, String> erros = new HashMap<>();
        UtilFunctions.validarPaginacao(page, size, erros);

        Set<String> camposPermitidos = Set.of("id", "titulo", "descricao", "prioridade", "status", "deadline", "responsavelId", "page", "size");

        Map<String, String> mapeamentoCampos = Map.of(
                "id", "id",
                "titulo", "titulo",
                "descricao", "descricao",
                "prioridade", "prioridade",
                "status", "status",
                "deadline", "deadline",
                "responsavelId", "responsavel_id"
        );

        UtilFunctions.validarCamposDeFiltro(filtros, erros, List.copyOf(camposPermitidos));

        filtros.forEach((campo, valor) -> {
            try {
                if (campo.equals("prioridade")) Prioridade.fromString(valor);
                else if (campo.equals("status")) StatusTarefa.fromString(valor);
            } catch (Exception e) {
                erros.put(campo, "Valor inválido para enum: " + valor);
            }
        });

        if (!erros.isEmpty()) {
            List<Map<String, String>> detalhes = erros.entrySet().stream()
                    .map(e -> Map.of("campo", e.getKey(), "erro", e.getValue()))
                    .toList();
            throw new InvalidParametersException(detalhes);
        }

        Map<String, String> filtrosConvertidos = new HashMap<>();
        filtros.forEach((campo, valor) -> {
            String campoReal = mapeamentoCampos.get(campo);
            if (campoReal != null) {
                filtrosConvertidos.put(campoReal, valor);
            }
        });

        Pageable pageable = PageRequest.of(page, size);
        Page<Tarefa> pagina = taskRepository.findAll(TaskSpecification.comFiltros(filtrosConvertidos), pageable);

        List<TaskResponseDTO> dados = pagina.getContent().stream()
                .map(TaskResponseDTO::converte)
                .toList();

        return new PageResponseDTO<>(new InfoPageDTO<>(pagina), dados);
    }

    public MetricasDTO buscarMetricas() {
        Usuario user = jwtTokenUtil.getUsuarioLogado();
        List<Tarefa> listTarefas = taskRepository.findByResponsavel(user);
        return MetricasDTO.converter(listTarefas);
    }

}
