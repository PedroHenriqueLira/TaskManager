package com.project.tasks.api.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends CustomRuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(String title, String message) {
        super(title, message);
    }

    public UnauthorizedException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public UnauthorizedException(String title, String message, HttpStatus httpStatus) {
        super(title, message, httpStatus);
    }

    @Override
    protected String defineTitle() {
        return "Unauthorized";
    }

    @Override
    protected HttpStatus defineHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}

