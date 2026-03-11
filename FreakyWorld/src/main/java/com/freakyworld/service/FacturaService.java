package com.freakyworld.service;

import com.freakyworld.domain.DetalleFactura;
import com.freakyworld.domain.Factura;
import com.freakyworld.repository.DetalleFacturaRepository;
import com.freakyworld.repository.FacturaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final DetalleFacturaRepository detalleFacturaRepository;

    public FacturaService(FacturaRepository facturaRepository,
                          DetalleFacturaRepository detalleFacturaRepository) {
        this.facturaRepository = facturaRepository;
        this.detalleFacturaRepository = detalleFacturaRepository;
    }

    @Transactional(readOnly = true)
    public Factura obtenerFactura(Long idFactura) {
        return facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<DetalleFactura> listarDetalles(Long idFactura) {
        return detalleFacturaRepository.findByFacturaIdFactura(idFactura);
    }
}