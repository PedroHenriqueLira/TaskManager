package com.project.tasks.api.utils;

import java.util.List;
import java.util.Map;

public class UtilFunctions {

    public static void validarCamposDeFiltro(Map<String, String> filtros, Map<String, String> erros, List<String> camposPermitidos) {
        for (String campo : filtros.keySet()) {
            if (!camposPermitidos.contains(campo)) {
                erros.put(campo, "Parâmetro inválido;");
            }
        }
    }

    public static void validarPaginacao(int page, int size, Map<String, String> erros) {
        if (size <= 0 || size > 100) {
            erros.put("size", "O size deve ser entre 1 e 100;");
        }

        if (page < 0) {
            erros.put("page", "O page deve ser maior ou igual a 0;");
        }
    }
}