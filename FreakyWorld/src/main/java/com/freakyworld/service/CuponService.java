package com.freakyworld.service;

import com.freakyworld.domain.Cupon;
import com.freakyworld.repository.CuponRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CuponService {

    private final CuponRepository cuponRepository;

    public CuponService(CuponRepository cuponRepository) {
        this.cuponRepository = cuponRepository;
    }

    @Transactional(readOnly = true)
    public List<Cupon> listarCupones() {
        return cuponRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cupon obtenerCuponPorId(Long idCupon) {
        return cuponRepository.findById(idCupon)
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado"));
    }

    @Transactional
    public void guardar(Cupon cupon) {
        if (cupon.getCodigo() == null || cupon.getCodigo().isBlank()) {
            throw new RuntimeException("El código del cupón es obligatorio");
        }

        if (cupon.getDescuento() == null || cupon.getDescuento().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El descuento debe ser mayor que cero");
        }

        if (cupon.getDescuento().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("El descuento no puede ser mayor a 100");
        }

        cupon.setCodigo(cupon.getCodigo().trim().toUpperCase());

        if (cupon.getActivo() == null) {
            cupon.setActivo(true);
        }

        cuponRepository.save(cupon);
    }

    @Transactional
    public void cambiarEstado(Long idCupon) {
        Cupon cupon = obtenerCuponPorId(idCupon);
        cupon.setActivo(!cupon.getActivo());
        cuponRepository.save(cupon);
    }

    @Transactional(readOnly = true)
    public Cupon validarCupon(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }

        return cuponRepository.findByCodigoIgnoreCaseAndActivoTrue(codigo.trim())
                .orElseThrow(() -> new RuntimeException("El cupón no existe o no está activo"));
    }

    @Transactional(readOnly = true)
    public BigDecimal aplicarDescuento(BigDecimal total, Cupon cupon) {
        if (cupon == null) {
            return total;
        }

        BigDecimal porcentaje = cupon.getDescuento()
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        BigDecimal descuento = total.multiply(porcentaje);
        return total.subtract(descuento).setScale(2, RoundingMode.HALF_UP);
    }
}