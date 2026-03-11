package com.freakyworld.controller;

import com.freakyworld.domain.Usuario;
import com.freakyworld.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final UsuarioService usuarioService;

    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/nuevo")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro/nuevo";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
                                 RedirectAttributes redirectAttributes) {
        try {
            usuarioService.registrarCliente(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Cuenta creada correctamente");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro/nuevo";
        }
    }

    @GetMapping("/activa")
    public String activarCuenta() {
        return "registro/activa";
    }

    @GetMapping("/olvido")
    public String olvidoPassword() {
        return "registro/olvido";
    }
}
