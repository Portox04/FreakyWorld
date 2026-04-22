package com.freakyworld.service;

import com.freakyworld.domain.Carrito;
import com.freakyworld.domain.Cupon;
import com.freakyworld.domain.DetalleFactura;
import com.freakyworld.domain.Factura;
import com.freakyworld.domain.Item;
import com.freakyworld.domain.Producto;
import com.freakyworld.repository.CarritoRepository;
import com.freakyworld.repository.DetalleFacturaRepository;
import com.freakyworld.repository.FacturaRepository;
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
    private final FacturaRepository facturaRepository;
    private final DetalleFacturaRepository detalleFacturaRepository;
    private final CorreoService correoService;
    private final CuponService cuponService;

    public CarritoService(CarritoRepository carritoRepository,
                          ItemRepository itemRepository,
                          ProductoRepository productoRepository,
                          FacturaRepository facturaRepository,
                          DetalleFacturaRepository detalleFacturaRepository,
                          CorreoService correoService,
                          CuponService cuponService) {
        this.carritoRepository = carritoRepository;
        this.itemRepository = itemRepository;
        this.productoRepository = productoRepository;
        this.facturaRepository = facturaRepository;
        this.detalleFacturaRepository = detalleFacturaRepository;
        this.correoService = correoService;
        this.cuponService = cuponService;
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

    @Transactional
    public Factura pagarCarrito(Long idUsuario, String metodoPago, String codigoCupon) {
        Carrito carrito = obtenerCarritoPorUsuario(idUsuario);
        List<Item> items = itemRepository.findByCarritoIdCarrito(carrito.getIdCarrito());

        if (items.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (Item item : items) {
            Producto producto = item.getProducto();

            if (producto.getStock() == null || producto.getStock() < item.getCantidad()) {
                throw new RuntimeException("No hay stock suficiente para el producto: " + producto.getNombre());
            }

            BigDecimal subtotal = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);
        }

        Cupon cupon = cuponService.validarCupon(codigoCupon);
        BigDecimal totalFinal = cuponService.aplicarDescuento(total, cupon);
        BigDecimal descuentoAplicado = total.subtract(totalFinal);

        Factura factura = new Factura();
        factura.setUsuario(carrito.getUsuario());
        factura.setSubtotal(total);
        factura.setDescuentoAplicado(descuentoAplicado);
        factura.setCodigoCupon(cupon != null ? cupon.getCodigo() : null);
        factura.setTotal(totalFinal);
        factura.setMetodoPago(metodoPago);
        factura.setEstado("PENDIENTE");
        factura.setNumeroSeguimiento("PED-" + System.currentTimeMillis());

        Factura facturaGuardada = facturaRepository.save(factura);

        for (Item item : items) {
            Producto producto = item.getProducto();
            BigDecimal precioUnitario = producto.getPrecio();
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad()));

            DetalleFactura detalle = new DetalleFactura();
            detalle.setFactura(facturaGuardada);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotal);

            detalleFacturaRepository.save(detalle);

            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }

        itemRepository.deleteAll(items);

        try {
            String mensaje = "Hola " + facturaGuardada.getUsuario().getNombre() + ",\n\n"
                    + "Tu compra fue registrada correctamente en FreakyWorld.\n"
                    + "Número de pedido: " + facturaGuardada.getIdFactura() + "\n"
                    + "Método de pago: " + facturaGuardada.getMetodoPago() + "\n"
                    + "Estado: " + facturaGuardada.getEstado() + "\n"
                    + "Número de seguimiento: " + facturaGuardada.getNumeroSeguimiento() + "\n"
                    + "Subtotal: ₡" + facturaGuardada.getSubtotal() + "\n"
                    + "Descuento aplicado: ₡" + facturaGuardada.getDescuentoAplicado() + "\n"
                    + "Total: ₡" + facturaGuardada.getTotal() + "\n";

            if (cupon != null) {
                mensaje += "Cupón aplicado: " + cupon.getCodigo() + " (" + cupon.getDescuento() + "%)\n";
            }

            mensaje += "\nGracias por comprar con nosotros.";

            correoService.enviarCorreo(
                    facturaGuardada.getUsuario().getCorreo(),
                    "Confirmación de compra - FreakyWorld",
                    mensaje
            );
        } catch (Exception e) {
            System.out.println("No se pudo enviar el correo de confirmación: " + e.getMessage());
        }

        return facturaGuardada;
    }
}