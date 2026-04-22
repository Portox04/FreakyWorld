package com.freakyworld.controller;

import com.freakyworld.domain.Usuario;
import com.freakyworld.service.CarritoService;
import com.freakyworld.service.UsuarioService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.freakyworld.domain.DetalleFactura;
import com.freakyworld.domain.Factura;
import com.freakyworld.service.FacturaService;
import java.util.List;
import com.freakyworld.service.PdfFacturaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final UsuarioService usuarioService;
    private final FacturaService facturaService;
    private final PdfFacturaService pdfFacturaService;

    public CarritoController(CarritoService carritoService,
        UsuarioService usuarioService,
        FacturaService facturaService,
        PdfFacturaService pdfFacturaService) {
    this.carritoService = carritoService;
    this.usuarioService = usuarioService;
    this.facturaService = facturaService;
    this.pdfFacturaService = pdfFacturaService;
}

    @GetMapping("/listado")
    public String listado(Model model, Principal principal) {
        Long idUsuario = obtenerIdUsuario(principal);

        model.addAttribute("items", carritoService.listarItems(idUsuario));
        model.addAttribute("total", carritoService.calcularTotal(idUsuario));

        return "carrito/listado";
    }

    @GetMapping("/items")
    public String items(Model model, Principal principal) {
        Long idUsuario = obtenerIdUsuario(principal);

        model.addAttribute("items", carritoService.listarItems(idUsuario));
        model.addAttribute("total", carritoService.calcularTotal(idUsuario));

        return "carrito/items";
    }

    @GetMapping("/agregar/{idProducto}")
    public String agregar(@PathVariable Long idProducto,
                          RedirectAttributes redirectAttributes,
                          Principal principal) {
        try {
            Long idUsuario = obtenerIdUsuario(principal);
            carritoService.agregarProducto(idUsuario, idProducto, 1);
            redirectAttributes.addFlashAttribute("mensaje", "Producto agregado al carrito");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito/listado";
    }

    @GetMapping("/eliminar/{idProducto}")
    public String eliminar(@PathVariable Long idProducto,
                           RedirectAttributes redirectAttributes,
                           Principal principal) {
        try {
            Long idUsuario = obtenerIdUsuario(principal);
            carritoService.eliminarProducto(idUsuario, idProducto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado del carrito");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito/listado";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long idProducto,
                             @RequestParam Integer cantidad,
                             RedirectAttributes redirectAttributes,
                             Principal principal) {
        try {
            Long idUsuario = obtenerIdUsuario(principal);
            carritoService.actualizarCantidad(idUsuario, idProducto, cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Carrito actualizado correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito/listado";
    }

    @GetMapping("/vaciar")
    public String vaciar(RedirectAttributes redirectAttributes,
                         Principal principal) {
        try {
            Long idUsuario = obtenerIdUsuario(principal);
            carritoService.vaciarCarrito(idUsuario);
            redirectAttributes.addFlashAttribute("mensaje", "Carrito vaciado correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carrito/listado";
    }
    @PostMapping("/pagar")
public String pagar(@RequestParam(defaultValue = "SIN DEFINIR") String metodoPago,
                    RedirectAttributes redirectAttributes,
                    Principal principal) {
    try {
        Long idUsuario = obtenerIdUsuario(principal);
        Factura factura = carritoService.pagarCarrito(idUsuario, metodoPago);

        redirectAttributes.addFlashAttribute("mensaje", "Compra realizada correctamente");
        return "redirect:/carrito/factura/" + factura.getIdFactura();

    } catch (RuntimeException e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/carrito/listado";
    }
}


    @GetMapping("/factura/{idFactura}")
    public String verFactura(@PathVariable Long idFactura,
            Model model,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            Long idUsuario = obtenerIdUsuario(principal);

            Factura factura = facturaService.obtenerFactura(idFactura);

            if (!factura.getUsuario().getIdUsuario().equals(idUsuario)) {
                throw new RuntimeException("No tienes permiso para ver esta factura");
            }

            List<DetalleFactura> detalles = facturaService.listarDetalles(idFactura);

            model.addAttribute("factura", factura);
            model.addAttribute("detalles", detalles);

            return "carrito/factura";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito/listado";
        }
    }
    @GetMapping("/factura/{idFactura}/pdf")
public ResponseEntity<byte[]> descargarFacturaPdf(@PathVariable Long idFactura,
                                                  Principal principal) {
    Long idUsuario = obtenerIdUsuario(principal);

    Factura factura = facturaService.obtenerFactura(idFactura);

    if (!factura.getUsuario().getIdUsuario().equals(idUsuario)) {
        throw new RuntimeException("No tienes permiso para descargar esta factura");
    }

    List<DetalleFactura> detalles = facturaService.listarDetalles(idFactura);
    byte[] pdf = pdfFacturaService.generarPdf(factura, detalles);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura-" + factura.getIdFactura() + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
}
    private Long obtenerIdUsuario(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        Usuario usuario = usuarioService.buscarPorCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return usuario.getIdUsuario();
    }
}