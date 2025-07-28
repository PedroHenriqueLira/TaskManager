package com.project.tasks.api.repository;

import com.project.tasks.api.enums.Status;
import com.project.tasks.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailAndStatus(String email, Status status);

    Usuario getUsuariosByEmail(String email);

    Optional<Usuario> findByEmail(String email);
}
