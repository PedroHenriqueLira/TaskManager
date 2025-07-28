package com.project.tasks.api.service;

import com.project.tasks.api.configs.security.JwtTokenUtil;
import com.project.tasks.api.model.Auditoria;
import com.project.tasks.api.repository.AuditoriaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class AuditoriaService {
    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Transactional(rollbackOn = Exception.class)
    public void salvarAuditoria(String token, String acao, String endpoint, int statusCode) {

        String usuario = getUsernameFromToken(token);

        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario);
        auditoria.setAcao(acao);
        auditoria.setEndpoint(endpoint);
        auditoria.setStatusCode(statusCode);
        auditoria.setDataCadastro(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));

        auditoriaRepository.save(auditoria);

    }

    private String getUsernameFromToken(String token) {
        return jwtTokenUtil.getUsernameFromToken(token);
    }
}

