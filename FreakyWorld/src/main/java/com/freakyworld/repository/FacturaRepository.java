package com.freakyworld.repository;

import com.freakyworld.domain.Factura;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {

    List<Factura> findByUsuarioIdUsuario(Long idUsuario);

    List<Factura> findByEstado(String estado);

    List<Factura> findByUsuarioIdUsuarioAndEstado(Long idUsuario, String estado);
}