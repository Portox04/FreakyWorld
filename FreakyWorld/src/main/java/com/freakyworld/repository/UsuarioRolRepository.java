package com.freakyworld.repository;

import com.freakyworld.domain.UsuarioRol;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

    List<UsuarioRol> findByUsuarioIdUsuario(Long idUsuario);
}