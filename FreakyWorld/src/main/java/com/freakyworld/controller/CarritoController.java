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

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final UsuarioService usuarioService;

    public CarritoController(CarritoService carritoService,
                             UsuarioService usuarioService) {
        this.carritoService = carritoService;
        this.usuarioService = usuarioService;
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

    private Long obtenerIdUsuario(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        Usuario usuario = usuarioService.buscarPorCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return usuario.getIdUsuario();
    }
}