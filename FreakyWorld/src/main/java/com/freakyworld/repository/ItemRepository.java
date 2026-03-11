package com.freakyworld.repository;

import com.freakyworld.domain.Item;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByCarritoIdCarrito(Long idCarrito);

    Optional<Item> findByCarritoIdCarritoAndProductoIdProducto(Long idCarrito, Long idProducto);
}