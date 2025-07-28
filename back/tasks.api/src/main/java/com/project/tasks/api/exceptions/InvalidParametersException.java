package com.project.tasks.api.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InvalidParametersException extends CustomRuntimeException {
    private final List<Map<String, String>> detalhes;

    public InvalidParametersException(String message) {
        super(message);
        this.detalhes = Collections.emptyList();
    }

    public InvalidParametersException(String message, Throwable cause) {
        super(message, cause);
        this.detalhes = Collections.emptyList();
    }

    public InvalidParametersException(String title, String message) {
        super(title, message);
        this.detalhes = Collections.emptyList();
    }

    public InvalidParametersException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
        this.detalhes = Collections.emptyList();
    }

    public InvalidParametersException(String title, String message, HttpStatus httpStatus) {
        super(title, message, httpStatus);
        this.detalhes = Collections.emptyList();
    }

    public InvalidParametersException(List<Map<String, String>> detalhes) {
        super("Parâmetros inválidos");
        this.detalhes = detalhes != null ? detalhes : Collections.emptyList();
    }

    public List<Map<String, String>> getDetalhes() {
        return detalhes;
    }

    @Override
    protected String defineTitle() {
        return "Invalid Parameters";
    }

    @Override
    protected HttpStatus defineHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
