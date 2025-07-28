package com.project.tasks.api.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends CustomRuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException(String title, String message) {
        super(title, message);
    }

    public ForbiddenException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public ForbiddenException(String title, String message, HttpStatus httpStatus) {
        super(title, message, httpStatus);
    }

    @Override
    protected String defineTitle() {
        return "Forbidden";
    }

    @Override
    protected HttpStatus defineHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
