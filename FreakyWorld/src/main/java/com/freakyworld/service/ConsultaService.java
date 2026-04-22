package com.freakyworld.service;

import com.freakyworld.domain.Producto;
import com.freakyworld.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultaService {

    private final ProductoRepository productoRepository;

    public ConsultaService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> listarProductosActivos() {
        return productoRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Producto> filtrar(String nombre, Long idCategoria,
                                  BigDecimal precioMin, BigDecimal precioMax) {

        List<Producto> productos = productoRepository.findByActivoTrue();

        return productos.stream()
                .filter(producto -> nombre == null || nombre.isBlank()
                        || producto.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(producto -> idCategoria == null
                        || (producto.getCategoria() != null
                        && producto.getCategoria().getIdCategoria().equals(idCategoria)))
                .filter(producto -> precioMin == null
                        || producto.getPrecio().compareTo(precioMin) >= 0)
                .filter(producto -> precioMax == null
                        || producto.getPrecio().compareTo(precioMax) <= 0)
                .collect(Collectors.toList());
    }
}