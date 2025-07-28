package com.project.tasks.api.configs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tasks.api.exceptions.UnauthorizedException;
import com.project.tasks.api.utils.ResponsePadraoDTO;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@Order(2)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final List<String> PUBLIC_URIS = List.of(
            "/auth",
            "/auth/",
            "/css", "/css/",
            "/html", "/html/",
            "/js", "/js/"
    );

    private static final List<String> PUBLIC_URIS_PREFIXES = List.of(
            "/auth",
            "/rec",
            "/actuator",
            "/css",
            "/html",
            "/js"
    );


    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (isPublicUri(uri) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String token = jwtTokenUtil.getTokenFromRequest();

        if (PUBLIC_URIS_PREFIXES.stream().anyMatch(uri::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        if (token == null || !jwtTokenUtil.validateToken(token)) {
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou inválido.");
            return;
        }

        try {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);

            List<String> permissions = claims.get("permissions", List.class);
            List<SimpleGrantedAuthority> authorities = (permissions == null)
                    ? Collections.emptyList()
                    : permissions.stream().map(SimpleGrantedAuthority::new).toList();

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username, null, authorities
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(request, response);

        } catch (Exception e) {
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Erro de autenticação: " + e.getMessage());
        }
    }

    private boolean isPublicUri(String uri) {
        return PUBLIC_URIS.stream().anyMatch(uri::startsWith);
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String errorMessage) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = mapper.writeValueAsString(ResponsePadraoDTO.falha(errorMessage));
        response.getWriter().write(json);
    }
}
