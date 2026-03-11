package com.freakyworld.repository;

import com.freakyworld.domain.Resena;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByProductoIdProducto(Long idProducto);

    List<Resena> findByUsuarioIdUsuario(Long idUsuario);
}