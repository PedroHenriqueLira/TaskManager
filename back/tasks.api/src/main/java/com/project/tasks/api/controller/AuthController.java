package com.project.tasks.api.controller;

import com.project.tasks.api.configs.security.JwtTokenUtil;
import com.project.tasks.api.dto.CreateUserRequestDTO;
import com.project.tasks.api.dto.LoginRequestDTO;
import com.project.tasks.api.enums.Status;
import com.project.tasks.api.model.Usuario;
import com.project.tasks.api.repository.UsuarioRepository;
import com.project.tasks.api.service.UsuarioService;
import com.project.tasks.api.utils.MsgCods;
import com.project.tasks.api.utils.ResponsePadraoDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService userService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticateUser(@RequestBody @Valid LoginRequestDTO authRequest) {
        Usuario user = usuarioRepository.findByEmailAndStatus(authRequest.getEmail(), Status.ATIVO)
                .orElseThrow(() -> new BadCredentialsException(new MsgCods().getCodigoErro(1)));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());

        authenticationManager.authenticate(authenticationToken);

        String token = jwtTokenUtil.generateToken(authRequest.getEmail(), user.getTipoUser());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId", user.getId());
        response.put("userEmail", user.getEmail());
        response.put("status", "Sucesso");
        response.put("type", "Bearer Token");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<ResponsePadraoDTO> createUser (@RequestBody @Valid CreateUserRequestDTO dto){
        ResponsePadraoDTO result = userService.createUser(dto);
        return ResponseEntity.ok().body(result);
    }

}
