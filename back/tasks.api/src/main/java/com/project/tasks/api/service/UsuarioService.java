package com.project.tasks.api.service;

import com.project.tasks.api.dto.CreateUserRequestDTO;
import com.project.tasks.api.enums.Status;
import com.project.tasks.api.exceptions.BadRequestException;
import com.project.tasks.api.model.Usuario;
import com.project.tasks.api.repository.UsuarioRepository;
import com.project.tasks.api.utils.MsgCods;
import com.project.tasks.api.utils.ResponsePadraoDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional(rollbackOn = Exception.class)
    public ResponsePadraoDTO createUser(@Valid CreateUserRequestDTO dto) {
        Optional<Usuario> user = usuarioRepository.findByEmailAndStatus(dto.getEmail(), Status.ATIVO);
        if (user.isPresent()) {
            throw new BadRequestException(new MsgCods().getCodigoErro(2));
        }

        Usuario newUser = new Usuario();
        newUser.setNome(dto.getNome());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setDocumento(dto.getDocumento());
        newUser.setEndereco(dto.getEndereco());
        newUser.prePersist();

        usuarioRepository.save(newUser);

        return ResponsePadraoDTO.sucesso("Usuário cadastrado com sucesso!");
    }
}
