package com.freakyworld.repository;

import com.freakyworld.domain.Carrito;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuarioIdUsuario(Long idUsuario);
}