package com.freakyworld.service;

import com.freakyworld.domain.Carrito;
import com.freakyworld.domain.Item;
import com.freakyworld.domain.Producto;
import com.freakyworld.repository.CarritoRepository;
import com.freakyworld.repository.ItemRepository;
import com.freakyworld.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemRepository itemRepository;
    private final ProductoRepository productoRepository;

    public CarritoService(CarritoRepository carritoRepository,
                          ItemRepository itemRepository,
                          ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.itemRepository = itemRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public Carrito obtenerCarritoPorUsuario(Long idUsuario) {
        return carritoRepository.findByUsuarioIdUsuario(idUsuario)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario"));
    }

    @Transactional(readOnly = true)
    public List<Item> listarItems(Long idUsuario) {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);
        return itemRepository.findByCarritoIdCarrito(carrito.getIdCarrito());
    }

    @Transactional
    public void agregarProducto(Long idUsuario, Long idProducto, Integer cantidad) {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (cantidad == null || cantidad <= 0) {
            cantidad = 1;
        }

        Item item = itemRepository.findByCarritoIdCarritoAndProductoIdProducto(
                carrito.getIdCarrito(), idProducto).orElse(null);

        if (item == null) {
            item = new Item();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setCantidad(cantidad);
        } else {
            item.setCantidad(item.getCantidad() + cantidad);
        }

        itemRepository.save(item);
    }

    @Transactional
    public void eliminarProducto(Long idUsuario, Long idProducto) {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);

        Item item = itemRepository.findByCarritoIdCarritoAndProductoIdProducto(
                carrito.getIdCarrito(), idProducto)
                .orElseThrow(() -> new RuntimeException("El producto no está en el carrito"));

        itemRepository.delete(item);
    }

    @Transactional
    public void actualizarCantidad(Long idUsuario, Long idProducto, Integer cantidad) {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);

        Item item = itemRepository.findByCarritoIdCarritoAndProductoIdProducto(
                carrito.getIdCarrito(), idProducto)
                .orElseThrow(() -> new RuntimeException("El producto no está en el carrito"));

        if (cantidad == null || cantidad <= 0) {
            itemRepository.delete(item);
        } else {
            item.setCantidad(cantidad);
            itemRepository.save(item);
        }
    }

    @Transactional
    public void vaciarCarrito(Long idUsuario) {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);
        List<Item> items = itemRepository.findByCarritoIdCarrito(carrito.getIdCarrito());
        itemRepository.deleteAll(items);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotal(Long idUsuario) {
        List<Item> items = listarItems(idUsuario);
        BigDecimal total = BigDecimal.ZERO;

        for (Item item : items) {
            BigDecimal subtotal = item.getProducto().getPrecio()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);
        }

        return total;
    }
}