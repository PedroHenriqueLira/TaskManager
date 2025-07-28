package com.project.tasks.api.utils;

public class ResponsePadraoDTO {

    private String status;
    private String message;

    public static ResponsePadraoDTO falha(String message) {
        ResponsePadraoDTO response = new ResponsePadraoDTO();
        response.setStatus("Falha");
        response.setMessage(message);
        return response;
    }

    public static ResponsePadraoDTO sucesso(String message) {
        ResponsePadraoDTO response = new ResponsePadraoDTO();
        response.setStatus("Sucesso");
        response.setMessage(message);
        return response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
