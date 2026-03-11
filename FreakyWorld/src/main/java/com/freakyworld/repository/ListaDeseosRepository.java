package com.freakyworld.repository;

import com.freakyworld.domain.ListaDeseos;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListaDeseosRepository extends JpaRepository<ListaDeseos, Long> {

    List<ListaDeseos> findByUsuarioIdUsuario(Long idUsuario);

    Optional<ListaDeseos> findByUsuarioIdUsuarioAndProductoIdProducto(Long idUsuario, Long idProducto);
}