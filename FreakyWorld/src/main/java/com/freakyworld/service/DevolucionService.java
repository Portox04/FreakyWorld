package com.freakyworld.service;

import com.freakyworld.domain.DetalleFactura;
import com.freakyworld.domain.Devolucion;
import com.freakyworld.domain.Factura;
import com.freakyworld.domain.Usuario;
import com.freakyworld.repository.DetalleFacturaRepository;
import com.freakyworld.repository.DevolucionRepository;
import com.freakyworld.repository.FacturaRepository;
import com.freakyworld.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DevolucionService {

    private final DevolucionRepository devolucionRepository;
    private final FacturaRepository facturaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DetalleFacturaRepository detalleFacturaRepository;

    public DevolucionService(DevolucionRepository devolucionRepository,
                             FacturaRepository facturaRepository,
                             UsuarioRepository usuarioRepository,
                             DetalleFacturaRepository detalleFacturaRepository) {
        this.devolucionRepository = devolucionRepository;
        this.facturaRepository = facturaRepository;
        this.usuarioRepository = usuarioRepository;
        this.detalleFacturaRepository = detalleFacturaRepository;
    }

    @Transactional
    public Devolucion solicitarDevolucion(Long idUsuario, Long idFactura,
                                          Long idDetalleFactura, String motivo,
                                          String descripcion) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        if (!factura.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("No puedes solicitar devolución de una factura que no te pertenece");
        }

        DetalleFactura detalleFactura = detalleFacturaRepository.findById(idDetalleFactura)
                .orElseThrow(() -> new RuntimeException("Detalle de factura no encontrado"));

        if (!detalleFactura.getFactura().getIdFactura().equals(idFactura)) {
            throw new RuntimeException("El producto no pertenece a la factura seleccionada");
        }

        if (motivo == null || motivo.isBlank()) {
            throw new RuntimeException("Debe indicar un motivo de devolución");
        }

        Devolucion devolucion = new Devolucion();
        devolucion.setUsuario(usuario);
        devolucion.setFactura(factura);
        devolucion.setDetalleFactura(detalleFactura);
        devolucion.setMotivo(motivo);
        devolucion.setDescripcion(descripcion);
        devolucion.setEstado("SOLICITADA");

        return devolucionRepository.save(devolucion);
    }

    @Transactional(readOnly = true)
    public List<Devolucion> listarPorUsuario(Long idUsuario) {
        return devolucionRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Devolucion> listarTodas() {
        return devolucionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Devolucion obtenerDevolucion(Long idDevolucion) {
        return devolucionRepository.findById(idDevolucion)
                .orElseThrow(() -> new RuntimeException("Devolución no encontrada"));
    }

    @Transactional
    public Devolucion cambiarEstado(Long idDevolucion, String estado) {
        Devolucion devolucion = devolucionRepository.findById(idDevolucion)
                .orElseThrow(() -> new RuntimeException("Devolución no encontrada"));

        if (estado == null || estado.isBlank()) {
            throw new RuntimeException("Debe indicar un estado");
        }

        devolucion.setEstado(estado);
        return devolucionRepository.save(devolucion);
    }
}