package com.freakyworld.repository;

import com.freakyworld.domain.Devolucion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {

    List<Devolucion> findByUsuarioIdUsuario(Long idUsuario);

    List<Devolucion> findByFacturaIdFactura(Long idFactura);

    List<Devolucion> findByEstado(String estado);
}