package com.freakyworld.repository;

import com.freakyworld.domain.DetalleFactura;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {

    List<DetalleFactura> findByFacturaIdFactura(Long idFactura);
}