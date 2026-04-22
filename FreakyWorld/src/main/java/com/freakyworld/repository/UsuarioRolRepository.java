package com.freakyworld.repository;

import com.freakyworld.domain.UsuarioRol;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

    List<UsuarioRol> findByUsuarioIdUsuario(Long idUsuario);

    boolean existsByUsuarioIdUsuarioAndRolIdRol(Long idUsuario, Long idRol);

    Optional<UsuarioRol> findByUsuarioIdUsuarioAndRolIdRol(Long idUsuario, Long idRol);
}