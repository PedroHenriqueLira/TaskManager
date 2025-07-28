package com.project.tasks.api.utils;

import com.project.tasks.api.enums.Prioridade;
import com.project.tasks.api.enums.StatusTarefa;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;

@Data
public class MsgCods {
    private HashMap<Integer, String> mapaCodigosErro = new HashMap<>();
    private String msgAuxiliar;

    public MsgCods() {
        this.msgAuxiliar = "";
        preencherCodigosErro();
    }

    public String getCodigoErro(Integer codErro) {
        if (mapaCodigosErro.containsKey(codErro)) {
            return this.mapaCodigosErro.get(codErro);
        }
        return "Erro não documentado!";
    }

    private void preencherCodigosErro() {
        mapaCodigosErro.put(1, "Erro - Falha de Busca - Dados do Usuário não encontrados.");
        mapaCodigosErro.put(2, "Erro - Falha ao Cadastrar - Email já está sendo utilizado por outro usuário.");
        mapaCodigosErro.put(3, "Erro - Falha ao Cadastrar - Prioridade inválida. Valores válidos: " + Arrays.toString(Prioridade.values()) + ".");
        mapaCodigosErro.put(4, "Erro - Falha ao Deletar - Dados não encontrado para o id informado.");
        mapaCodigosErro.put(5, "Erro - Falha ao Deletar - Usuário sem permissão para deletar a tarefa informada.");
        mapaCodigosErro.put(6, "Erro - Falha ao Editar - Dados não encontrado para o id informado.");
        mapaCodigosErro.put(7, "Erro - Falha ao Editar - Usuário sem permissão para editar a tarefa informada.");
        mapaCodigosErro.put(8, "Erro - Falha ao Editar - A data limite deve ser maior ou igual a data atual.");
        mapaCodigosErro.put(9, "Erro - Falha ao Editar - Status inválido. Valores válidos: " + Arrays.toString(StatusTarefa.values()) + ".");


    }

}
