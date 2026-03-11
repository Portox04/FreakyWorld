package com.freakyworld.controller;

import com.freakyworld.domain.DetalleFactura;
import com.freakyworld.domain.Factura;
import com.freakyworld.domain.Usuario;
import com.freakyworld.service.DevolucionService;
import com.freakyworld.service.FacturaService;
import com.freakyworld.service.UsuarioService;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/devolucion")
public class DevolucionController {

    private final DevolucionService devolucionService;
    private final FacturaService facturaService;
    private final UsuarioService usuarioService;

    public DevolucionController(DevolucionService devolucionService,
                                FacturaService facturaService,
                                UsuarioService usuarioService) {
        this.devolucionService = devolucionService;
        this.facturaService = facturaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/nueva/{idFactura}")
    public String formulario(@PathVariable Long idFactura,
                             Model model,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        try {
            Long idUsuario = obtenerIdUsuario(principal);

            Factura factura = facturaService.obtenerFactura(idFactura);

            if (!factura.getUsuario().getIdUsuario().equals(idUsuario)) {
                throw new RuntimeException("No puedes solicitar devolución de una factura que no te pertenece");
            }

            List<DetalleFactura> detalles = facturaService.listarDetalles(idFactura);

            model.addAttribute("factura", factura);
            model.addAttribute("detalles", detalles);

            return "devolucion/formulario";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuario/pedidos";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Long idFactura,
                          @RequestParam Long idDetalleFactura,
                          @RequestParam String motivo,
                          @RequestParam(required = false) String descripcion,
                          Principal principal,
                          RedirectAttributes redirectAttributes) {
        try {
            Long idUsuario = obtenerIdUsuario(principal);

            devolucionService.solicitarDevolucion(
                    idUsuario,
                    idFactura,
                    idDetalleFactura,
                    motivo,
                    descripcion
            );

            redirectAttributes.addFlashAttribute("exito", "Solicitud de devolución enviada correctamente");
            return "redirect:/devolucion/mis";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/devolucion/nueva/" + idFactura;
        }
    }

    @GetMapping("/mis")
    public String misDevoluciones(Model model,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        try {
            Long idUsuario = obtenerIdUsuario(principal);
            model.addAttribute("devoluciones", devolucionService.listarPorUsuario(idUsuario));
            return "devolucion/mis";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuario/perfil";
        }
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