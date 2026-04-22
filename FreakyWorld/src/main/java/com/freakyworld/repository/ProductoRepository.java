package com.freakyworld.repository;

import com.freakyworld.domain.Producto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);


    List<Producto> findByCategoriaIdCategoria(Long idCategoria);

    List<Producto> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);
    
    List<Producto> findTop8ByCategoriaIdCategoriaAndIdProductoNotAndActivoTrue(Long idCategoria, Long idProducto);

    List<Producto> findByActivoTrue();
}