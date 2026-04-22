package com.freakyworld.controller;

import com.freakyworld.domain.Rol;
import com.freakyworld.domain.Usuario;
import com.freakyworld.domain.UsuarioRol;
import com.freakyworld.service.UsuarioService;
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
@RequestMapping("/admin")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin/listado";
    }

    @GetMapping("/roles/{idUsuario}")
    public String verRoles(@PathVariable Long idUsuario, Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorId(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<UsuarioRol> usuarioRoles = usuarioService.listarRolesDeUsuario(idUsuario);
            List<Rol> roles = usuarioService.listarRoles();

            model.addAttribute("usuario", usuario);
            model.addAttribute("usuarioRoles", usuarioRoles);
            model.addAttribute("roles", roles);

            return "admin/roles";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/listado";
        }
    }

    @PostMapping("/roles/asignar")
    public String asignarRol(@RequestParam Long idUsuario,
                             @RequestParam Long idRol,
                             RedirectAttributes redirectAttributes) {
        try {
            usuarioService.asignarRol(idUsuario, idRol);
            redirectAttributes.addFlashAttribute("exito", "Rol asignado correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/roles/" + idUsuario;
    }

    @PostMapping("/roles/quitar")
    public String quitarRol(@RequestParam Long idUsuario,
                            @RequestParam Long idRol,
                            RedirectAttributes redirectAttributes) {
        try {
            usuarioService.quitarRol(idUsuario, idRol);
            redirectAttributes.addFlashAttribute("exito", "Rol quitado correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/roles/" + idUsuario;
    }
}