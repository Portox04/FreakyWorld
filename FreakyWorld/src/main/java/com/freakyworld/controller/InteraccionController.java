package com.freakyworld.controller;

import com.freakyworld.domain.ListaDeseos;
import com.freakyworld.domain.Producto;
import com.freakyworld.domain.Resena;
import com.freakyworld.domain.Usuario;
import com.freakyworld.repository.ListaDeseosRepository;
import com.freakyworld.repository.ResenaRepository;
import com.freakyworld.service.ProductoService;
import com.freakyworld.service.UsuarioService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/interaccion")
public class InteraccionController {

    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final ListaDeseosRepository listaDeseosRepository;
    private final ResenaRepository resenaRepository;

    public InteraccionController(UsuarioService usuarioService,
                                 ProductoService productoService,
                                 ListaDeseosRepository listaDeseosRepository,
                                 ResenaRepository resenaRepository) {
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.listaDeseosRepository = listaDeseosRepository;
        this.resenaRepository = resenaRepository;
    }

    @GetMapping("/deseos/agregar/{idProducto}")
    public String agregarDeseo(@org.springframework.web.bind.annotation.PathVariable Long idProducto,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(principal);
            Producto producto = obtenerProducto(idProducto);

            boolean yaExiste = listaDeseosRepository
                    .findByUsuarioIdUsuarioAndProductoIdProducto(usuario.getIdUsuario(), idProducto)
                    .isPresent();

            if (!yaExiste) {
                ListaDeseos deseo = new ListaDeseos();
                deseo.setUsuario(usuario);
                deseo.setProducto(producto);
                listaDeseosRepository.save(deseo);
                redirectAttributes.addFlashAttribute("mensaje", "Producto agregado a lista de deseos");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "El producto ya estaba en tu lista de deseos");
            }

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/producto/ficha/" + idProducto;
    }

    @GetMapping("/deseos/eliminar/{idProducto}")
    public String eliminarDeseo(@org.springframework.web.bind.annotation.PathVariable Long idProducto,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(principal);

            ListaDeseos deseo = listaDeseosRepository
                    .findByUsuarioIdUsuarioAndProductoIdProducto(usuario.getIdUsuario(), idProducto)
                    .orElseThrow(() -> new RuntimeException("El producto no está en la lista de deseos"));

            listaDeseosRepository.delete(deseo);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado de la lista de deseos");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/usuario/deseos";
    }

   @PostMapping("/resena/guardar")
public String guardarResena(@RequestParam Long idProducto,
                            @RequestParam Integer calificacion,
                            @RequestParam(required = false) String comentario,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
    try {
        System.out.println("ENTRO A GUARDAR RESENA");
        System.out.println("idProducto = " + idProducto);
        System.out.println("calificacion = " + calificacion);
        System.out.println("comentario = " + comentario);

        Usuario usuario = obtenerUsuario(principal);
        Producto producto = obtenerProducto(idProducto);

        System.out.println("usuario = " + usuario.getIdUsuario());
        System.out.println("producto = " + producto.getIdProducto());

        if (calificacion == null || calificacion < 1 || calificacion > 5) {
            throw new RuntimeException("La calificación debe estar entre 1 y 5");
        }

        Resena resena = new Resena();
        resena.setUsuario(usuario);
        resena.setProducto(producto);
        resena.setCalificacion(calificacion);
        resena.setComentario(comentario);

        resenaRepository.save(resena);

        System.out.println("RESENA GUARDADA, ID = " + resena.getIdResena());

        redirectAttributes.addFlashAttribute("mensaje", "Reseña guardada correctamente");

    } catch (Exception e) {
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("error", "Error al guardar reseña: " + e.getMessage());
    }

    return "redirect:/producto/ficha/" + idProducto;
}

    private Usuario obtenerUsuario(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        return usuarioService.buscarPorCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private Producto obtenerProducto(Long idProducto) {
        return productoService.buscarPorId(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }
}