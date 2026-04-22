package com.freakyworld.controller;

import com.freakyworld.domain.Factura;
import com.freakyworld.domain.ListaDeseos;
import com.freakyworld.domain.Usuario;
import com.freakyworld.repository.FacturaRepository;
import com.freakyworld.repository.ListaDeseosRepository;
import com.freakyworld.service.CorreoService;
import com.freakyworld.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final FacturaRepository facturaRepository;
    private final ListaDeseosRepository listaDeseosRepository;
    private final CorreoService correoService;

    public UsuarioController(UsuarioService usuarioService,
                             FacturaRepository facturaRepository,
                             ListaDeseosRepository listaDeseosRepository,
                             CorreoService correoService) {
        this.usuarioService = usuarioService;
        this.facturaRepository = facturaRepository;
        this.listaDeseosRepository = listaDeseosRepository;
        this.correoService = correoService;
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Principal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(principal);
            model.addAttribute("usuario", usuario);
            return "usuario/perfil";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(@RequestParam("nombre") String nombre,
                                   @RequestParam("correo") String correo,
                                   @RequestParam(value = "direccion", required = false) String direccion,
                                   @RequestParam(value = "password", required = false) String password,
                                   Principal principal,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = obtenerUsuario(principal);

            String correoActual = usuario.getCorreo();
            boolean cambioCorreo = !correoActual.equals(correo);
            boolean cambioPassword = password != null && !password.isBlank();

            usuarioService.actualizarPerfil(
                    usuario.getIdUsuario(),
                    nombre,
                    correo,
                    direccion,
                    password
            );

            if (cambioCorreo || cambioPassword) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null) {
                    new SecurityContextLogoutHandler().logout(request, null, auth);
                }
                return "redirect:/login?actualizado";
            }

            redirectAttributes.addFlashAttribute("exito", "Cuenta actualizada correctamente");
            return "redirect:/usuario/perfil";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuario/perfil";
        }
    }

    @PostMapping("/eliminar")
    public String eliminarCuenta(Principal principal,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(principal);

            usuarioService.eliminarCuenta(usuario.getIdUsuario());

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, null, auth);
            }

            redirectAttributes.addFlashAttribute("exito", "Tu cuenta fue eliminada correctamente");
            return "redirect:/login";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuario/perfil";
        }
    }

    @GetMapping("/pedidos")
    public String pedidos(Model model, Principal principal,
                          RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(principal);
            List<Factura> pedidos = facturaRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
            model.addAttribute("pedidos", pedidos);
            return "usuario/pedidos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/deseos")
    public String deseos(Model model, Principal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(principal);
            List<ListaDeseos> deseos = listaDeseosRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
            model.addAttribute("deseos", deseos);
            return "usuario/deseos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/retorno")
    public String retorno() {
        return "usuario/retorno";
    }

    @GetMapping("/correo/prueba")
    @ResponseBody
    public String probarCorreo() {
        correoService.enviarCorreo(
                "freakyworld760@gmail.com",
                "Prueba de correo FreakyWorld",
                "Este es un correo de prueba enviado desde FreakyWorld."
        );
        return "Correo enviado";
    }

    private Usuario obtenerUsuario(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        String correo = null;

        if (principal instanceof Authentication authentication) {
            Object principalObj = authentication.getPrincipal();

            if (principalObj instanceof OAuth2User oauth2User) {
                correo = oauth2User.getAttribute("email");
            } else {
                correo = authentication.getName();
            }
        } else {
            correo = principal.getName();
        }

        if (correo == null || correo.isBlank()) {
            throw new RuntimeException("No se pudo identificar el correo del usuario autenticado");
        }

        return usuarioService.buscarPorCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}