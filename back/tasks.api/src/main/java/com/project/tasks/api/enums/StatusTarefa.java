package com.project.tasks.api.enums;

import com.project.tasks.api.exceptions.BadRequestException;
import com.project.tasks.api.utils.MsgCods;

import java.util.Arrays;

public enum StatusTarefa {
    ANDAMENTO,
    CONCLUIDA;

    public static StatusTarefa fromString(String valor) {
        return Arrays.stream(StatusTarefa.values())
                .filter(p -> p.name().equalsIgnoreCase(valor))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(new MsgCods().getCodigoErro(9)));
    }
}
