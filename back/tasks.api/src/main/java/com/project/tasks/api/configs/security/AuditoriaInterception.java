package com.project.tasks.api.configs.security;

import com.project.tasks.api.service.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class AuditoriaInterception implements HandlerInterceptor {

    @Autowired
    private AuditoriaService auditoriaService;

    private static final List<String> PUBLIC_ROUTES = Arrays.asList(
            "/actuator/health"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestUri = request.getRequestURI();

        if (isPublicRoute(requestUri)) return;

        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7).trim();
            String httpMethod = request.getMethod();

            String action = getActionFromHttpMethod(httpMethod)
                    + " - Metodo: " + getControllerMethodName(handler);

            int statusCode = response.getStatus();

            auditoriaService.salvarAuditoria(token, action, "Rota: " + requestUri, statusCode);
        }
    }

    private String getActionFromHttpMethod(String httpMethod) {
        return switch (httpMethod) {
            case "GET" -> "Buscar";
            case "POST" -> "Cadastrar";
            case "PUT", "PATCH" -> "Editar";
            case "DELETE" -> "Excluir";
            default -> "Ação não identificada";
        };
    }

    private String getControllerMethodName(Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            return handlerMethod.getMethod().getName();
        }
        return "Método não encontrado";
    }

    private boolean isPublicRoute(String uri) {
        return PUBLIC_ROUTES.stream().anyMatch(uri::startsWith);
    }
}

